package neuroidnet.ntr;

import edu.ull.cgunay.utils.plots.*;
import neuroidnet.periphery.*;
import edu.ull.cgunay.utils.*;
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
public class Neuroid implements Input, Serializable, Expressive {
    
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
     * Sequence of <code>Neuroid</code> in the array held in <code>Area</code>.
     * Good for representation.
     * Used to override Object.hashCode() and don't pass around this id, but 
     * hascode is dynamic and changes at every runtime, leaving us with a volatile identifier.
     * Oh, well. Set in <code>Area.addNeuroid()</code>
     * @see Area#neuroids
     * @see Area#addNeuroid
     */
    final int id; 
    
    /**
     * Get the value of id.
     * @return value of id.
     */
    public int getId() {
	return id;
    }
  
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

	id = area.addNeuroid(this);
	//init();
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

	id = area.addNeuroid(this);
	//init();
    }

    /**
     * Called by all constructors.
     */
    final private void init() {

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
		UninterruptedIteration.loop(synapses.iterator(), new Task() { 
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
	new UninterruptedIteration() {
	    public void job(Object o) {
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
	System.out.println("Fire at t=" + Network.numberFormat.format(area.time) + " " +
			   getStatus());
	timeLastFired = area.time;
	area.fireNeuroid(this);
	spikeTrain.add(new Double(timeLastFired)); 
	if (!watch) {
	    new UninterruptedIteration() {
		public void job(Object o) throws TaskException {
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

	mode.step();
    }

    /**
     * Iterator task class which scans synapses of the neuroid.
     * 
     * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
     * @version 1.0
     * @since 1.0
     * @see edu.ull.cgunay.utils.Task
     */
    abstract class SynapseActivityTask implements edu.ull.cgunay.utils.TaskWithReturn {
	Neuroid toplevel = Neuroid.this;

	SynapseActivityTask() {	}

	/**
	 * Initiates the iteration over synapses of the enclosing Neuroid.
	 *
	 */
	public void iterate() {
	    UninterruptedIteration.loop(synapses, this); 
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
	
	// Beware: local variable
	Concept concept = (Concept) area.network.conceptArea.get(conceptSet); 

	try {
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

	// if no exception was raised
	this.concept = concept;
    }

    /**
     * Add <code>synapse</code> to <code>synapses</code> and return index to become
     * the id of the <code>Synapse</code>. Analogous to <code>Area.addNeuroid()</code>
     *
     * @param synapse a <code>Synapse</code> to add to <code>synapses</code>
     * @return an <code>int</code> value, the index of new synapse in <code>synapses</code>
     * @see Area#addNeuroid
     */
    public int addSynapse(Synapse synapse) {
	synapses.add(synapse);
	return synapses.indexOf(synapse);
    }

    /**
     * Identify the neuroid with its <code>id</code> and <code>area</code>.
     *
     * @return a <code>String</code> value
     */
    public String toString() {
	String name = getClass().getName();
	return name.substring(name.lastIndexOf('.') + 1) + " #" + id + " (in " + area + ")";
    }

    /**
     * Output of <code>toString()</code> plus potential,
     * mode status, last fire time, and concept name.
     *
     * @return a <code>String</code> value
     */
    public String getStatus() {
	return
	    this + " (" + mode.getStatus() + "), u = " +
	    Network.numberFormat.format(potential) + 
	    ", fired = " + Network.numberFormat.format(timeLastFired) + 
	    (concept != null ? ", " + concept : "");
    }

    /**
     * Describe in higher detail. List all contained synapses.
     *
     * @return a <code>String</code> value
     */
    public String getProperties() {
	return
	    this.getStatus() +
	    new StringTask(" {\n", "\n}") {
		public void job(Object o) {
		    super.job("\t" + ((Expressive)o).getStatus() +
			      " (from " + ((Synapse)o).srcNeuroid.getStatus() + ")\n");
		}
	    }.getString(synapses);
    }

    /**
     * Dump synaptic activity to output (matlab format). Called by:
     * TODO: optionally in gnuplot format?
     * @deprecated Use plots and the profile gathering system.
     * @see ConceptArea#dumpData
     */
    public String dumpData() {
	return null /*new StringTask() {
		// spike lists from different synapses separated by space
		public void job(Object o) {
		    this.retval += ((Synapse)o).dumpData() + " "; 
		}
	    }.getString(synapses)*/;
    }

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
    protected class Mode extends Profilable implements Serializable, Expressive {
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
	
	
	/**
	 * Strip fully qualified class name to leave only the class name.
	 *
	 * @return a <code>String</code> value
	 */
	public String toString() {
	    String name = getClass().getName();
	    return name.substring(name.lastIndexOf('.') + 1);
	}

	/**
	 * <code>toString()</code> plus state, threshold,
	 * suggested threshold, and fitnessCounter.
	 *
	 */
	public String getStatus() {
	    return
		this + ": state = " + /*stateList[*/ state /*]*/ +
		", T = " + Network.numberFormat.format(threshold) +
		", fit = " + fitnessCounter +
		", sugT = " + Network.numberFormat.format(suggestedThreshold) /*+
		", sumOW = " + Network.numberFormat.format(sumOfCurrentWeights)*/;
	}

	/**
	 * Nothing special.
	 *
	 * @return a <code>String</code> value
	 */
	public String getProperties() {
	    return getStatus();
	}

	public double doubleValue() {
	    throw new RuntimeException("Not applicable to this object");
	} 


	/**
	 * Takes care of mode related changes to the neuroid. Called from Neuroid.step()
	 * @see Neuroid#step
	 */
	public void step() {
	    switch (state) {
	    case AM:
		if (potential >= threshold) {
		    state = AM1;
		    fitnessCounter = fitnessCounter+1;
		    updateWeights();
		    calculatePotential();
		    suggestedThreshold = potential;
		    setChanged();
		}	     
		break;
	    
	    case AM1:
		if (potential < suggestedThreshold) {
		    // if any presynaptic neuroid has fired in this period
		    try {
			Iteration.loop(synapses.iterator(), new Task() {
			    public void job(Object o) throws TaskException {
				if (((Synapse)o).isPotentiated()) 
				    throw new BreakOutOfIterationException();
			    }}); 
		    } catch (BreakOutOfIterationException e) {
			fitnessCounter = fitnessCounter - 1; // Correct behavior
			// NO: If fitnessCounter<=1 go back to AM (no other change in mode)
			if (fitnessCounter <= 1) {
			    fitnessCounter = 1;
			    //state = AM;
			    //break;
			} // end of if (mode.fitnessCounter <= 0)
			updateWeights();
			calculatePotential();
			suggestedThreshold = potential;
			setChanged();
		    } // end of try-catch
		} else { // i.e. if (potential >= mode.suggestedThreshold)
		    if (fitnessCounter < correctTimesRequired - 1) {
			fitnessCounter = fitnessCounter + 1;
			updateWeights();
			calculatePotential();
			suggestedThreshold = potential;
			setChanged();
		    } else { // i.e. if (mode.fitnessCounter >= correctTimesRequired - 1)
			try {
			    makeConcept(); // Throws the exception
			    state = UM; // Memorized!
			    // Set threshold lower than anticipated
			    threshold = 0.9 * suggestedThreshold; 
			    System.out.println("Into UM mode!; " + this);
			    setChanged();
			} catch (ConceptSaturatedException e) {
			    // Reset neuroid back to AM mode
			    reset();
			} 
		    } // end of else of if (fitnessCounter < correctTimesRequired - 1)
		} // end of else of if (potential < suggestedThreshold)
		break;
	    
	    case UM:
		if (potential >= threshold) {
		    fire();
		} else if (debug && potential >= 0.5) {
		    System.out.println("Not enough activity in: " + this);
		} // end of else
	    
		break;
	    
	    default:
		throw new RuntimeException("Undefined Mode.state for " + this);
	    } // end of switch (state)

	    //System.out.println("In step of " + this);
	    notifyObservers(new Double(area.time));	// if changed, notify observers of mode
	}

	/**
	 * Resets the neuroid to its initial <i>pristine</i> state.
	 * Sets the mode to AM and initializes the weights to 1.
	 * Called from step().
	 * @see #step
	 */
	public void reset() {
	    //debug = true;		// Enable debugging for neuroids that're reset

	    if (debug) 
		System.out.println("Resetting " + this);
	
	    // Available memory state
	    // AM -> AM1 threshold
	    // TODO: Just set the state!
	    state = AM;
	    threshold = area.getActivationThreshold();
	    setChanged();

	    // Iterate over synapses and set weights to 1
	    UninterruptedIteration.loop(synapses.iterator(), new Task() {
		    public void job(Object o) {
			((Synapse) o).setWeight(1);
		    }});
	}

    }
}
