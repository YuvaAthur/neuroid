package neuroidnet.ntr;

import neuroidnet.ntr.plots.*;
import neuroidnet.periphery.*;
import neuroidnet.utils.*;
//import neuroidnet.remote.*;

import java.lang.*;
import java.util.*;
import java.text.*;
import java.io.*;
//import java.rmi.*;


// $Id$
/**
 * First attempt to implement the central neuroidal entity with
 * programmability capability. Also includes simple soma functionality of
 * a spiking neuron model, specifically the spike response model as
 * defined in Maass and Bishop 1999.
 * Instances are contained in <code>Area</code>s.
 * 
 * @see Area
 *
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version $Revision$ for this file
 * @since 1.0
 */
public class Neuroid implements Input, Serializable {
    
    /**
     * Membrane potential.
     */
    double potential;

    /**
     * List of incoming synapses.
     */
    protected Dendrite synapses = new Dendrite();

    /**
     * Last firing time of this Neuroid.
     */
    double timeLastFired; // TODO: make time a class?

    /**
     * Learning parameter for the Winnow learning algorithm.
     * @see Neuroid#updateWeights
     */
    public static double correctTimesRequired = 3;

    /**
     * 
     */
    double refractoryTimeConstant;
    
    /**
     * Get the value of refractoryTimeConstant.
     * @return value of refractoryTimeConstant.
     */
    public double getRefractoryTimeConstant() {
	return refractoryTimeConstant;
    }
    
    /**
     * Set the value of refractoryTimeConstant.
     * @param v  Value to assign to refractoryTimeConstant.
     */
    public void setRefractoryTimeConstant(double  v) {
	this.refractoryTimeConstant = v;
    }

    /**
     * Constant external current for all neuroids in network (not used!)
     */
    double externalCurrent = 2;

    /**
     * Parent Area.
     */
    protected Area area;
    
    /**
       * Get the value of area.
       * @return value of area.
       */
    public Area getArea() {return area;}
    
    /**
       * Set the value of area.
       * @param v  Value to assign to area.
       */
    public void setArea(Area  v) {this.area = v;}
    

    /**
     * Associated concept if neuroid has memorized anything.
     * @see Concept
     */
    protected Concept concept = null;

    /**
     * Sequence of <code>Neuroid</code> in <code>Area</code>. Good for representation.
     * Used to override Object.hashCode() and don't pass around this id, but 
     * hascode is dynamic and changes at everyruntime, leaving us with a volatile identifier.
     * Oh, well. Set in <code>Area.addNeuroid()</code>
     * @see Area#neuroids
     * @see Area#addNeuroid
     */
    public int id; 
  
    /**
     * Represents the state and dynamic parameters of the neuroid.
     */
    protected Mode mode;
    
    /**
     * Get the value of mode.
     * @return value of mode.
     */
    public Mode getMode() {
	return mode;
    }
    
    /**
     * Set the value of mode and connects any profilers if watched.
     * @param v  Value to assign to mode.
     */
    public void setMode(Mode  v) {
	this.mode = v;
	try {
	    profile.getModeProfile().connectTo(mode, new Double(area.time)); 
	} catch (NullPointerException e) { } // it's ok, then don't do it.
    }
    

    /**
     * Internal.
     */
    boolean hasFired;


    /**
     * Internal.
     */
    double sumOfCurrentWeights;

    /**
     * If set, outputs debugging info about this neuroid.
     */
    boolean debug = false;


    /**
     * If set, the neuroid saves informatoin about its state changes.
     */
    protected boolean watch = false;
    
    /**
     * Get the value of watch.
     * @return value of watch.
     */
    public boolean isWatch() {
	return watch;
    }
    
    /**
     * Set the value of watch. if watched, create a <code>NeuroidProfile</code>
     * object.
     * @param v  Value to assign to watch.
     */
    public void setWatch(boolean  v) {
	this.watch = v;
	if (watch) 
	    profile = new NeuroidProfile(this);
    }

    NeuroidProfile profile;
    
    /**
     * Get the value of profile.
     * @return value of profile.
     */
    public NeuroidProfile getProfile() {
	return profile;
    }
    
    /**
     * Set the value of profile.
     * @param v  Value to assign to profile.
     */
    public void setProfile(NeuroidProfile  v) {
	this.profile = v;
    }

    /**
     * List of times that the neuroid fired.
     * Redundant with the <code>spikeTrain</code> defined in <code>Synapse</code>.
     * @see Synapse#spikeTrain
     */
    Vector spikeTrain = new Vector(); // TODO: make time a class?
    
    /**
     * Get the value of spikeTrain.
     * @return value of spikeTrain.
     */
    public Vector getSpikeTrain() {
	return spikeTrain;
    }
    
    /**
     * Set the value of spikeTrain.
     * @param v  Value to assign to spikeTrain.
     */
    public void setSpikeTrain(Vector  v) {
	this.spikeTrain = v;
    }

    /**
     * Keeps track of mode changes, instances of mode object and time pairs
     */
    Profile modeChanges;

    /**
     * Dummy constructor.
     *
     */
    public Neuroid(Area area) {
	this.area = area;

	// Available memory state w/ threshold 
	setMode(new Mode(Mode.AM, Double.POSITIVE_INFINITY)); 

	init();
    }

    /**
     * Sets initial parameters. 
     * (Not used: Calculates <code>sumOfWeights</code> from
     * period length given by the <code>Area</code>.)
      @see Neuroid.Mode#sumOfWeights
      @see Area#period
     * @param area an <code>Area</code> value
     * @param refractoryTimeConstant
     */
    public Neuroid(Area area, double initialThreshold, double refractoryTimeConstant) {
	this.area = area;
	this.refractoryTimeConstant = refractoryTimeConstant;

	// TODO: get threshold out of mode because it's no longer modifyable by algorithms, or else put everything in Mode.
	setMode(new Mode(Mode.AM, initialThreshold)); // Available memory state w/ threshold 
	
	// Set spiking neuron parameters
	externalCurrent = 2;
	timeLastFired = - 1000;	// Initially fired a long time ago

	// Find sumOfWeights from given period value of the Area (DISCONTINUED)
	mode.setSumOfWeights(0.0);
/*	    -externalCurrent * ( externalCurrent - mode.threshold ) *
	    Math.exp( -area.period );*/

	init();
    }

    /**
     * Called by all constructors.
     */
    final private void init() {
	area.addNeuroid(this);
    }

    /**
     * Aid method to get a period for initiating calculations in <code>Network</code>.
     * Initial period is calculated using network parameter values taken from Hopfield & Herz 95.
     * @see Network
     * @return a <code>double</code> value
     */
    public static double defaultPeriod() {
	double			// Local copies of parameters!
	    externalCurrent = 2,
	    sumOfWeights = 0.96,
	    threshold = 1;
	return Math.log((externalCurrent - sumOfWeights) /
			(externalCurrent - threshold));
    }

    /**
     * Returns Address of the neuroid.
     * OBSOLETE!
     * @see Neuroid.Address
     *
     * @return an <code>Address</code> value
     */
/*    public Address getAddress() {
	return new Address(area.id, id);
    }*/
    
    /**
     * Method of java.lang.Object that return codes for hash table entries.
     * OBSOLETE: why do we need this?
     * @return an <code>int</code> value
     */
/*    public int hashCode() {
	return id;
    }
*/
    /**
     * Method of java.lang.Object that is used in many utils.
     * OSOLETE: why do we need this?
     * @param obj Another Neuroid.
     * @see Neuroid
     * @return True if two Neuroids have the same <code>id</code>.
     * @see Neuroid#id
     */
/*    public boolean equals(Object obj) {
	return id == ((Neuroid) obj).id;
    }
*/
    /**
     * Refractory kernel taken from Maass and Bishop 1999, Eq. 1.47 on p.31.
     * Keeps equivalence to integrate and fire model.
     *
     * @param time a <code>double</code> value
     * @return a <code>double</code> potential value
     */
    double refractoriness(double time) {
	return (time > 0) ? - mode.getThreshold() * Math.exp(-time/refractoryTimeConstant) : 0;
    }

    /**
     * Iterate through Synapses and add kernel values and decrease refractoriness
     * at this time value.
     * TODO: add external current!
     * @see #fire
     */
    void calculatePotential() {
	while (true) {
	    try {
		potential = 0;
		Iteration.loop(synapses.iterator(), new Task() { 
			public void job(Object o) {
			    potential += ((Synapse)o).getPotential();
			}});
		break;
	    } catch (ConcurrentModificationException e) {
		// do nothing, i.e. restart
		System.out.println("Concurrent modification in Neuroid.calculatePotential()" +
				   ", repeating...");
	    } 
	} // of while
	// TODO: refractoriness can be calculated by looking at previous firing times in spikeTrain
	//double refr = refractoriness(area.time - timeLastFired); 
	//potential += refr;

	// Refractory effect for all spikes emitted (see #fire())
	new Iteration() {
	    public void job(Object o) throws IterationException {
		double spikeTime = ((Double)o).doubleValue();
		potential += refractoriness(area.time - spikeTime);
	    }
	}.loop(spikeTrain);

/*	if (refr < 0) 
	    System.out.println("Refractoriness = " + Network.numberFormat.format(refr) +
			       ", at time = " + Network.numberFormat.format(area.time) +
			       ", with constant = " + refractoryTimeConstant); */
    }

    /**
     * Fires the neuroid. Neuroid contact container Area to propagate the spike along its axon.
     * Saves firing time to <code>spikeTrain</code> if being watched.
     * @see Area#fireNeuroid
     * @see #watch
     */
    public void fire() {
	System.out.println("Fire " + this);
	timeLastFired = area.time;
	area.fireNeuroid(this);
	spikeTrain.add(new Double(timeLastFired)); 
	if (!watch) {
	    new Iteration() {
		public void job(Object o) throws IterationException {
		    double spikeTime = ((Double)o).doubleValue();
		    if (spikeTime < (area.time - 3 * refractoryTimeConstant)) 
			throw new RemoveFromIterationException(); // Remove spike from list
		    else 
			throw new BreakOutOfIterationException(); // End iteration
		}
	    }.loop(spikeTrain);

	    // REDUNDANT, remove one! See note above in calculatePotential
	    //profile.spikesEmitted.add(new Double(timeLastFired)); 
	}
    }

    /**
     * Update state of the <code>Neuroid</code>.
     * Use the UMT algorithm by default, though behavior will depend
     * on <code>Synapse</code> parameters.

     * @see Synapse
     */
    public void step() {
	
	calculatePotential();

	/*if (potential >= 0.5) {
	    System.out.println("Activity in: " + this);
	} */

	switch (mode.getState()) {
	case Mode.AM:
	    if (potential >= mode.getThreshold()) {
		mode.setState(Mode.AM1);
		mode.setFitnessCounter(mode.getFitnessCounter()+1);
		updateWeights();
		calculatePotential();
		mode.setSuggestedThreshold(potential);
	    }	     
	    break;
	    
	case Mode.AM1:
	    if (potential < mode.getSuggestedThreshold()) {
		// if any presynaptic neuroid has fired in this period
		hasFired = false;
		Iteration.loop(synapses.iterator(), new Task() {
		    public void job(Object o) {
			if (((Synapse)o).isPotentiated()) 
			    hasFired = true;
		    }});
		if (hasFired) {
		    mode.setFitnessCounter(mode.getFitnessCounter() - 1); // Correct behavior
		    // NO: If fitnessCounter<=1 go back to AM (no other change in mode)
		    if (mode.getFitnessCounter() <= 1) {
			mode.setFitnessCounter(1);
			//mode.setState(Mode.AM);
			//break;
		    } // end of if (mode.fitnessCounter <= 0)
		    updateWeights();
		    calculatePotential();
		    mode.setSuggestedThreshold(potential);
		} // end of if (hasFired)
	    } else { // i.e. if (potential >= mode.suggestedThreshold)
		if (mode.getFitnessCounter() < correctTimesRequired - 1) {
		    mode.setFitnessCounter(mode.getFitnessCounter() + 1);
		    updateWeights();
		    calculatePotential();
		    mode.setSuggestedThreshold(potential);
		} else { // i.e. if (mode.fitnessCounter >= correctTimesRequired - 1)
		    try {
			makeConcept(); // Throws the exception
			mode.setState(Mode.UM); // Memorized!
			// Set threshold lower than anticipated
			mode.setThreshold(0.9 * mode.getSuggestedThreshold()); 
			System.out.println("Into UM mode!; " + this);
		    } catch (ConceptSaturatedException e) {
			// Reset neuroid back to AM mode
			reset();
			// Following is not a very good idea...
			timeLastFired = area.time; // Enable refractory effect by fake firing... 
		    } 

		} // end of else of if (mode.fitnessCounter < correctTimesRequired - 1)
		
	    } // end of else of if (potential < mode.suggestedThreshold)
	    
	    break;

	    
	case Mode.UM:
	    if (potential >= mode.getThreshold()) {
		fire();
	    } else if (debug && potential >= 0.5) {
		System.out.println("Not enough activity in: " + this);
	    } // end of else
	    
	    break;
	    
	default:
	    throw new RuntimeException("Undefined Mode.state for " + this);
	} // end of switch (mode.getState())

	//System.out.println("In step of " + this);
	mode.notifyObservers(new Double(area.time));	// if changed, notify observers of mode
    }

    /**
     * Iterator task class which scans synapses of the neuroid.
     * 
     * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
     * @version 1.0
     * @since 1.0
     * @see neuroidnet.utils.Task
     */
    abstract class SynapseActivityTask implements neuroidnet.utils.TaskWithReturn {
	Neuroid toplevel = Neuroid.this;

	SynapseActivityTask() {	}

	/**
	 * Initiates the iteration over synapses of the enclosing Neuroid.
	 *
	 */
	public void iterate() {
	    Iteration.loop(synapses.iterator(), this); 
	}

	/**
	 * Calls <code>potentiatedSynapse</code> and <code>silentSynapse</code>
	 * deciding by scanning <code>Synapse</code>s.
	 *
	 * @param o an <code>Object</code> value
	 * @see Synapse
	 * @see #potentiatedSynapse
	 * @see #silentSynapse
	 */
	public void job(Object o) {
	    Synapse s = (Synapse) o;
	    if (s.isPotentiated())
		potentiatedSynapse(s);
	    else 
		silentSynapse(s);
	} 

	/**
	 * Called for each potentiated synapse.
	 *
	 * @param s a <code>Synapse</code> value
	 */
	abstract void potentiatedSynapse(Synapse s);

	/**
	 * Called for each non-potentiated synapse.
	 *
	 * @param s a <code>Synapse</code> value
	 */
	abstract void silentSynapse(Synapse s);
    }

    /**
     * Updates presynaptic weights according to a variant of Winnow learning rule.
     * <!--Normalizes weights to <code>sumOfWeights</code> to keep <code>period</code> fixed.-->
     * @see Neuroid#period
     * @see Neuroid#sumOfWeights
     */
    void updateWeights() {
	if (debug) 
	    System.out.println("updateWeights: " + this + "{");
	
	sumOfCurrentWeights = 0; // currently not used

	// Loop over synapses and update weights according to Winnow learning rule
	new SynapseActivityTask() {
	    double weight;
	    public void job(Object o) {
		Synapse s = (Synapse) o;
		if (debug)
		    System.out.println("\t" + s + " from " + s.srcNeuroid);
		weight = s.getWeight();
		super.job(s);	// main job
		toplevel.sumOfCurrentWeights += weight;
		s.weight.notifyObservers(new Double(area.time));
	    }
	    // TODO: needs saturation mechanism
	    void potentiatedSynapse(Synapse s) {
		s.setWeight( weight * toplevel.correctTimesRequired /
			     toplevel.mode.fitnessCounter);
	    }
	    // TODO: needs saturation mechanism
	    void silentSynapse(Synapse s) {
		s.setWeight( weight * toplevel.mode.fitnessCounter /
			     toplevel.correctTimesRequired);
	    }
	    public Object getValue() { return null; } // N/A
	}.iterate();

	if (debug) 
	    System.out.println("}");

	if (sumOfCurrentWeights == 0) 
	    return;
/*	TODO: fix externalCurrent calculation first then enable the following:
	// Loop over synapses and normalize weights sumOfWeights calculated before
	Iteration.loop(synapses.iterator(), new Task() { 
		void job(Object o) {
		    o.weight *= sumOfWeights/sumOfCurrentWeights;
		}});*/
    }

    /**
     * Create new concept.
     * Trace back synapses that made this neuroid go to UM mode to find concept set.
     * Then look for associated concept in conceptArea,
     * otherwise create new Concept using this concept set.
     * <!-- In case concept is saturated, then the neuroid is reset() -->
     * <p>TODO: destroy old <code>concept</code> if it exists.
     * @exception ConceptSaturatedException 
     * @see ConceptArea
     * @see Concept
     * @see ConceptSaturatedException
     * @see #reset()
     */
    void makeConcept() throws ConceptSaturatedException {

	// Loop over synapses and save their concepts in conceptSet
	SynapseActivityTask synapseIterator = new SynapseActivityTask() {
		HashSet conceptSet;

		public void iterate() {
		    conceptSet = new HashSet();
		    super.iterate();
		}

		public void job(Object o) {
		    super.job(o);	// main job
		}

		void potentiatedSynapse(Synapse s) {
		    conceptSet.addAll(s.srcNeuroid.concept.getConceptSet());
		}

		void silentSynapse(Synapse s) { }

		public Object getValue() { return conceptSet; }
	    };

	synapseIterator.iterate();

	// Collect the result of the iteration over the synapses.
	HashSet conceptSet = (HashSet) synapseIterator.getValue();

	if (conceptSet.size() == 0) {
	     System.out.println("EMPTY CONCEPT!: \n" + this.getStatus());
	     debug = true;	// Set debugging flag for this neuroid
	     area.network.addWatch(this);
	} 

	try {
	    concept = (Concept) area.network.conceptArea.get(conceptSet);
	    concept.equals(concept); // redundant operation to raise exception
	} catch (NullPointerException e) { 
	    concept = new ArtificialConcept(area.network, conceptSet);
	} 

	// An exception should be received in case the replication factor is exceeded.
	// refractory period is enabled without exhibiting spiking to suppress re-activation.
	// TODO2: Later make global inhibitory connections limit this number.
	// 	  One might randomly distribute the timeConstantS with deviation param
	//	  and the global inhibitory neuroid will suppress late concepts even
	// 	  from going into UM

	concept.attach(this);
    }

    /**
     * Resets the neuroid to its initial <i>pristine</i> state.
     * Sets the mode to AM and initializes the weights to 1.
     * Called from makeConcept().
     * @see #makeConcept
     */
    public void reset() {
	//debug = true;		// Enable debugging for neuroids that're reset

	if (debug) 
	     System.out.println("Resetting " + this);
	
	// Available memory state
	// AM -> AM1 threshold
	setMode(new Mode(Mode.AM, area.getActivationThreshold()));

	// Iterate over synapses and set weights to 1
	Iteration.loop(synapses.iterator(), new Task() {
		public void job(Object o) {
		    ((Synapse) o).setWeight(1);
		}});
    }

    /**
     * Method inherited from <code>java.lang.Object</code> to display the
     * <code>Neuroid</code>'s state in text format.
     *
     * @return a <code>String</code> value
     */
    public String toString() {
	return "Neuroid #" + id /*hashCode()*/ + " in " + area + ", u = " +
	    Network.numberFormat.format(potential) + ", " + mode +
	    ", fired = " + Network.numberFormat.format(timeLastFired) + 
	    (concept != null ? ", " + concept : "");
    }

    /**
     * Describe in higher detail. List all contained synapses.
     *
     * @return a <code>String</code> value
     */
    public String getStatus() {

	TaskWithReturn synapseIterator = new StringTask(" {\n") {
		public void job(Object o) {
		    retval += "\t" + o + " from " + ((Synapse)o).srcNeuroid + "\n";
		}
	    };
 
	Iteration.loop(synapses.iterator(), synapseIterator);

	return this + (String)synapseIterator.getValue() + "\n}";
    }

    /**
     * Dump synaptic activity to output (matlab format). Called by:
     * TODO: optionally in gnuplot format?
     * @see ConceptArea#dumpData
     */
    public String dumpData() {

	String retval = "";
	
	TaskWithReturn toStringTask = new StringTask() {
		// spike lists from different synapses separated by space
		public void job(Object o) {
		    this.retval += ((Synapse)o).dumpData() + " "; 
		}
	    };
	
	Iteration.loop(synapses.iterator(), toStringTask);
	
	retval += (String)toStringTask.getValue();

	//id++;
	return retval;	
    }

    /*public String dumpData() {

	String retval = "";
	
	// TODO: make this following class common with the one in Network.toString()
	TaskWithReturn toStringTask =
	    new TaskWithReturn() {
		    String retval = new String();
		    
		    // spike lists from different synapses separated by space
		    public void job(Object o) {
			retval += ((Synapse)o).dumpData() + " "; 
		    }
		    
		    public Object getValue() {
			return retval;
		    }
		};
	
	Iteration.loop(synapses.iterator(), toStringTask);
	
	retval += (String)toStringTask.getValue();

	//id++;
	return retval;	
    }*/

    /**
     * Address of a neuroid, conjunction of container area id and
     * neuroid id within the area.
     *
     * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
     * @version 1.0
     * @since 1.0
     */
/*    public class Address {
	int areaId, neuroidId;
    }
*/
    /**
     * Modes that a neuron can have.
     * <p>TODO: Make this a hierarchy of classes in a subpackage called umt
     *
     * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
     * @version 1.0
     * @since 1.0
     */
    protected class Mode extends Profilable implements Serializable {
	// The following lists should be consistent with each other.
	public final static int
	    AM = 0, 
	    UM = AM + 1,
	    AM1 = UM + 1;
	//public final static String stateList[] = { "AM", "UM", "AM1"};

	int fitnessCounter = 1;
	
	/**
	 * Get the value of fitnessCounter.
	 * @return value of fitnessCounter.
	 */
	public int getFitnessCounter() {
	    return fitnessCounter;
	}
	
	/**
	 * Set the value of fitnessCounter.
	 * @param v  Value to assign to fitnessCounter.
	 */
	public void setFitnessCounter(int  v) {
	    this.fitnessCounter = v;
	    setChanged();
	}

	double suggestedThreshold;
	
	/**
	 * Get the value of suggestedThreshold.
	 * @return value of suggestedThreshold.
	 */
	public double getSuggestedThreshold() {
	    return suggestedThreshold;
	}
	
	/**
	 * Set the value of suggestedThreshold.
	 * @param v  Value to assign to suggestedThreshold.
	 */
	public void setSuggestedThreshold(double  v) {
	    this.suggestedThreshold = v;
	    setChanged();
	}

	Mode(int state, double threshold) {
	    this.state = state;
	    this.threshold = threshold;
	}
	
	/**
	 * State of neuroid, can only take values defined here.
	 * TODO: Use byte?  
	 */
	int state;
	
	/**
	   * Get the value of state.
	   * @return value of state.
	   */
	public int getState() {return state;}
	
	/**
	   * Set the value of state.
	   * @param v  Value to assign to state.
	   */
	public void setState(int  v) {
	    this.state = v;
	    setChanged();
	}
	
	/**
	 * Threshold of the neuroid.
	 */
	double threshold;
	
	/**
	   * Get the value of threshold.
	   * @return value of threshold.
	   */
	public double getThreshold() {return threshold;}
	
	/**
	   * Set the value of threshold.
	   * @param v  Value to assign to threshold.
	   */
	public void setThreshold(double  v) {
	    this.threshold = v;
	    setChanged(); 
	}

	/**
	 * The total sum of incoming weights to a Neuroid should be fixed. (Why?)
	 * @see Neuroid#Neuroid
	 */
	double sumOfWeights;
	
	/**
	 * Get the value of sumOfWeights.
	 * @return value of sumOfWeights.
	 */
	public double getSumOfWeights() {
	    return sumOfWeights;
	}
	
	/**
	 * Set the value of sumOfWeights.
	 * @param v  Value to assign to sumOfWeights.
	 */
	public void setSumOfWeights(double  v) {
	    this.sumOfWeights = v;
	    setChanged();
	}
	
	public String toString() {
	    return "state = " + /*stateList[*/ state /*]*/ +
		", T = " + Network.numberFormat.format(threshold) +
		", fit = " + fitnessCounter +
		", sugT = " + Network.numberFormat.format(suggestedThreshold) /*+
		", sumOW = " + Network.numberFormat.format(sumOfCurrentWeights)*/;
	}

	public double doubleValue() {
	    throw new RuntimeException("Not applicable to this object");
	} 
    }
}
