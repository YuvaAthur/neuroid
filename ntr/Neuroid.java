package Base;
import Base.*;
//import Remote.*;
import java.lang.*;
import java.util.*;
import java.text.*;
//import java.rmi.*;
import Utils.*;

// $Id$
/**
 * First attempt to implement the central neuroidal entity with
 * programmability capability, also including simple soma functionality of
 * the spiking neuron model, specifically the spike response model as
 * defined in Maass and Bishop 1999.
 * Instances are contained in Areas.
 * 
 * @see Area
 *
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version $Revision$ for this file
 * @since 1.0
 */
public class Neuroid implements Input {
    /**
     * Membrane potential.
     */
    double potential;

    /**
     * List of incoming synapses.
     */
    Vector synapses = new Vector();

    /**
     * Last firing time of this Neuroid.
     */
    double timeLastFired; // TODO: make time a class?

    /**
     * Learning parameter for the Winnow learning algorithm.
     * @see Neuroid#updateWeights
     */
    double correctTimesRequired = 2;

    /**
     * 
     */
    double refractoryTimeConstant;

    /**
     * Constant external current for all neuroids in network (N/A)
     */
    double externalCurrent = 2;

    /**
     * Parent Area.
     */
    protected Area area;

    /**
     * Associated concept if neuroid has memorized anything.
     * @see Concept
     */
    protected Concept concept = null;

    /**
     * OBSOLETE? Good for representation.
     */
/*    public int id; // TODO: Override Object.hashCode() and don't pass around this id?!
  */  
    /**
     * Represents the state and dynamic parameters of the neuroid.
     */
    public Mode mode;

    /**
     * Internal.
     */
    boolean hasFired;

    /**
     * Internal.
     */
    double sumOfCurrentWeights;

    /**
     * For formatting real values
     */
    NumberFormat numberFormat = NumberFormat.getInstance();

    /**
     * Dummy constructor.
     *
     */
    public Neuroid(Area area) {
	this.area = area;

	mode = new Mode(Mode.AM, Double.POSITIVE_INFINITY); // Available memory state w/ threshold 

	init();
    }

    /**
     * Sets initial parameters. Calculates <code>sumOfWeights</code> from
     * period length given by the <code>Area</code>.
     * @see Neuroid.Mode#sumOfWeights
     * @see Area#period
     * @param area an <code>Area</code> value
     * @param id an <code>int</code> value
     * @param refractoryTimeConstant
     */
    public Neuroid(Area area, double initialThreshold, double refractoryTimeConstant) {
	this.area = area;
	this.refractoryTimeConstant = refractoryTimeConstant;

	// TODO: get threshold out of mode because it's no longer modifyable by algorithms, or else put everything in Mode.
	mode = new Mode(Mode.AM, initialThreshold); // Available memory state w/ threshold 
	
	// Set spiking neuron parameters
	externalCurrent = 2;
	timeLastFired = - 1000;	// Initially fired a long time ago

	// Find sumOfWeights from given period value of the Area
	mode.sumOfWeights =
	    -externalCurrent * ( externalCurrent - mode.threshold ) *
	    Math.exp( -area.period );

	init();
    }

    /**
     * Called by all constructors.
     */
    final private void init() {
	area.addNeuroid(this);
	numberFormat.setMaximumFractionDigits(3);
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
	return (time > 0) ? - mode.threshold * Math.exp(-time/refractoryTimeConstant) : 0;
    }

    /**
     * Iterate through Synapses and add kernel values and decrease refractoriness
     * at this time value.
     * TODO: add external current!
     */
    void calculatePotential() {
	potential = 0;

	Iteration.loop(synapses.iterator(), new Utils.Task() { 
		public void job(Object o) {
		    potential += ((Synapse)o).getPotential();
/*		    try {
			potential += ((o instanceof Synapse) ?
				      ((Synapse)o).getPotential() :
				      ((Remote.SynapseInt)o).getLocalSynapse().getPotential());
		    } catch (java.rmi.RemoteException e) {
			System.out.println("Remote.Synapse cannot be reached.");
		    }*/
		}});
	potential += refractoriness(area.time - timeLastFired);
    }

    /**
     * Fires the neuroid. Neuroid contact container Area to propagate the spike along its axon.
     * @see Area#fireNeuroid
     */
    public void fire() {
	System.out.println("Fire " + this);
	timeLastFired = area.time;
	area.fireNeuroid(this);
    }

    /**
     * Update state of the <code>Neuroid</code>.
     * Use the UMT algorithm by default, though behavior will depend
     * on <code>Synapse</code> parameters.
     * @see Synapse
     */
    public void step() {
	
	calculatePotential();

	switch (mode.getState()) {
	case Mode.AM:
	    if (potential >= mode.getThreshold()) {
		mode.setState(Mode.AM1);
		mode.fitnessCounter++;
		updateWeights();
		calculatePotential();
		mode.suggestedThreshold = potential;
	    }	     
	    break;
	    
	case Mode.AM1:
	    if (potential < mode.suggestedThreshold) {
		// if any presynaptic neuroid has fired in this period
		hasFired = false;
		Iteration.loop(synapses.iterator(), new Utils.Task() {
		    public void job(Object o) {
			if (((Synapse)o).isPotentiated()) 
			    hasFired = true;
		    }});
		if (hasFired) {
		    mode.fitnessCounter--; // Correct behavior
		    // TODO: if fitnessCounter ==0 go back to AM
		    updateWeights();
		    calculatePotential();
		    mode.suggestedThreshold = potential;
		} // end of if (hasFired)
	    } else { // i.e. if (potential >= mode.suggestedThreshold)
		if (mode.fitnessCounter < correctTimesRequired - 1) {
		    mode.fitnessCounter++;
		    updateWeights();
		    calculatePotential();
		    mode.suggestedThreshold = potential;
		} else { // i.e. if (mode.fitnessCounter >= correctTimesRequired - 1)
		    mode.setState(Mode.UM); // Memorized!
		    mode.threshold = mode.suggestedThreshold; // Set threshold
		    makeConcept();
		    System.out.println("Into UM mode!; " + this);
		} // end of else of if (mode.fitnessCounter < correctTimesRequired - 1)
		
	    } // end of else of if (potential < mode.suggestedThreshold)
	    
	    break;

	    
	case Mode.UM:
	    if (potential >= mode.getThreshold()) {
		fire();
	    }	    
	    break;
	    
	default:
	    throw new RuntimeException("Undefined Mode.state for " + this);
	} // end of switch (mode.getState())

	//System.out.println("In step of " + this);
    }

    /**
     * Iterator task class which scans synapses of the neuroid.
     * 
     * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
     * @version 1.0
     * @since 1.0
     * @see Utils.Task
     */
    abstract class SynapseActivityTask implements Utils.TaskWithReturn {
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
	 * @see potentiatedSynapse
	 * @see silentSynapse
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
     * Updates presynaptic weights according to Winnow learning rule.
     * Normalizes weights to <code>sumOfWeights</code> to keep <code>period</code> fixed.
     * @see Neuroid#period
     * @see Neuroid#sumOfWeights
     */
    void updateWeights() {
	sumOfCurrentWeights = 0;

	// Loop over synapses and update weights according to Winnow learning rule
	new SynapseActivityTask() {
		public void job(Object o) {
		    super.job(o);	// main job
		    Synapse s = (Synapse) o;
		    toplevel.sumOfCurrentWeights += s.weight;
		}
		void potentiatedSynapse(Synapse s) {
		    s.weight = s.weight * toplevel.correctTimesRequired /
			toplevel.mode.fitnessCounter;
		}
		void silentSynapse(Synapse s) {
		    s.weight = s.weight * toplevel.mode.fitnessCounter /
			toplevel.correctTimesRequired;
		}
		public Object getValue() { return null; } // N/A
	    }.iterate();

/*
	// Loop over synapses and update according to Winnow learning rule
	Iteration.loop(synapses.iterator(), new Utils.Task() { 
		public void job(Object o) {
		    Synapse s = (Synapse) o;
		    if (s.isPotentiated()) 
			s.weight = s.weight * correctTimesRequired /
			    mode.fitnessCounter;
		    else 
			s.weight = s.weight * mode.fitnessCounter /
			    correctTimesRequired;

		    sumOfCurrentWeights += s.weight;
		}});
*/
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
     * <p>TODO: destroy old <code>concept</code> if it exists.
     * @see ConceptArea
     * @see Concept
     */
    void makeConcept() {

	// Loop over synapses and update according to Winnow learning rule
	SynapseActivityTask synapseIterator = new SynapseActivityTask() {
		HashSet conceptSet;

		public void iterate() {
		    conceptSet = new HashSet();
		    super.iterate();
		}

		public void job(Object o) {
		    super.job(o);	// main job
		    Synapse s = (Synapse) o;
		    toplevel.sumOfCurrentWeights += s.weight;
		}

		void potentiatedSynapse(Synapse s) {
		    conceptSet.add(s.srcNeuroid.concept);
		}

		void silentSynapse(Synapse s) { }

		public Object getValue() { return conceptSet; }
	    };

	synapseIterator.iterate();

	// Collect the result of the iteration over the synapses.
	HashSet conceptSet = (HashSet) synapseIterator.getValue();

	try {
	    concept = (Concept) area.network.conceptArea.get(conceptSet);
	    concept.equals(concept); // redundant operation to raise exception
	} catch (NullPointerException e) { 
	    concept = new ArtificialConcept(area.network, conceptSet);
	} 
    }

    /**
     * Method inherited from java.lang.Object to display text about <code>Neuroid</code>.
     *
     * @return a <code>String</code> value
     */
    public String toString() {
	return "Neuroid #" + hashCode() + " in " + area + ", u = " +
	    numberFormat.format(potential) + ", " + mode +
	    (concept != null ? ", concept: " + concept : "");
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
     *
     * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
     * @version 1.0
     * @since 1.0
     */
    protected class Mode {
	public final static int
	    AM = 0, 
	    UM = AM + 1,
	    AM1 = UM + 1;

	int fitnessCounter = 1;
	double suggestedThreshold;

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
	public void setState(int  v) {this.state = v;}
	
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
	public void setThreshold(double  v) {this.threshold = v;}

	/**
	 * The total sum of incoming weights to a Neuroid should be fixed.
	 * @see Neuroid#Neuroid
	 */
	double sumOfWeights; 
	
	public String toString() {
	    return "state = " + state + ", T = " + threshold + ", sumOW = " + sumOfCurrentWeights;
	}
    }
}
