
package neuroidnet.ntr;

import edu.ull.cgunay.utils.plots.*;
import neuroidnet.periphery.*;
import edu.ull.cgunay.utils.*;

import java.util.*;

// $Id$
/**
 * First attempt to implement the central neuroidal entity with
 * programmability capability as defined by Valiant. Also includes
 * simple soma functionality of a spiking neuron model, specifically
 * the spike response model as defined in Maass and Bishop 1999.
 *
 * <p>Created: Thu May  2 22:00:37 2002
 * <p>Modified: $Date$
 *
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version $Revision$ for this file.
 */

public class SRMNeuroid extends Neuroid  {

    /**
     * Learning parameter for the Winnow learning algorithm.
     * @see SRMNeuroid.SRMMode#updateWeights
     */
    static final double correctTimesRequired = 3;

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
     * Internal.
     */
    double sumOfCurrentWeights;

    /**
     * Sets <code>mode</code> and calls <code>super()</code>.
     *
     */
    public SRMNeuroid (Area area) {
	super(area);

	// Available memory state w/ threshold 
	setMode(new Mode()); 
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
    public SRMNeuroid(Area area, double initialThreshold, double refractoryTimeConstant) {
	super(area);

	this.refractoryTimeConstant = refractoryTimeConstant;

	// TODO: get threshold out of mode because it's no longer
	// modifyable by algorithms, or else put everything in Mode.
	setMode(new Mode(initialThreshold)); // Available memory state w/ threshold 
	
	// Set spiking neuron parameters
	externalCurrent = 2;
	timeLastFired = - 1000;	// Initially fired a long time ago

	// Find sumOfWeights from given period value of the Area (DISCONTINUED)
	((Mode)mode).setSumOfWeights(0.0);
/*	    -externalCurrent * ( externalCurrent - mode.threshold ) *
	    Math.exp( -area.period );*/

    }

    /**
     * Saves firing time to <code>spikeTrain</code>. And removes old
     * spikes unless being watched.
     */
    public void fire() {
	super.fire();

	// if not watched
	if (!watch) {
	    // Add an entry here because super.fire() only adds if watched
	    spikeTrain.add(new Double(timeLastFired)); 

	    // But remove entries older than some refractoryTimeConstants
	    new UninterruptedIteration() {
		public void job(Object o) throws TaskException {
		    double spikeTime = ((Double)o).doubleValue();
		    if (spikeTime < (area.time - 3 * refractoryTimeConstant)) 
			throw new RemoveFromIterationException(); // Remove spike from list
		    else 
			throw new BreakOutOfIterationException(); // End iteration
		}
	    }.loop(spikeTrain);
	}
    }

    /**
     * Helper method to get a period for initiating calculations in <code>Network</code>.
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
    protected void calculatePotential() {
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

    

    protected class Mode extends Neuroid.Mode {
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
	 * Initializes to AM state and sets the <code>threshold</code> to be infinity.
	 *
	 */
	public Mode() {
	    super(AM, Double.POSITIVE_INFINITY);
	}

	/**
	 * Initializes to AM state and sets the <code>threshold</code>.
	 *
	 */
	public Mode(double threshold) {
	    super(AM, threshold);
	}

	/**
	 * Takes care of mode related changes to the neuroid. Called from Neuroid.step().
	 * @see Neuroid#step
	 */
	public void step() {
	    int watchdog = 5;	// Only three state changes allowed at a time for safety

	    try {
		while (watchdog-- > 0) {
		    int oldstate = state;
	    
		    switch (state) {
		    case AM:
			if (potential >= threshold) {
			    state = AM1;
			    fitter(); // No need, it'll be done in AM1
			}	     
			break;
	    
		    case AM1:
			if (potential < suggestedThreshold) 
			    isitnotfit();
			else { 
			    if (fitnessCounter < correctTimesRequired - 1) 
				fitter();
			    else 
				fit();	// Goes into UM
			} // end of else of if 
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

		    // Asynchronous behavior, process more if state change occurs
		    if (state == oldstate)
			break;	// out of while

		} // end of while (watchdog-- > 0)

	    } catch (ConceptSaturatedException e) {
		// Reset neuroid back to AM mode
		reset();
	    } 

	    if (watchdog <= 0) 
		throw new Error("Watchdog expired! " + SRMNeuroid.this.getStatus());

	    notifyObservers(new Double(area.time));	// if changed, notify observers of mode
	}

	void suggestThreshold() {
	    updateWeights();
	    calculatePotential();
	    suggestedThreshold = potential;
	    setChanged();
	}

	void fitter() {
	    fitnessCounter = fitnessCounter + 1;
	    suggestThreshold();
	}

	void fitless() {
	    fitnessCounter = fitnessCounter - 1; // Correct behavior
	    // NO: If fitnessCounter<=1 go back to AM (no other change in mode)
	    if (fitnessCounter <= 1) {
		fitnessCounter = 1;
		//state = AM;
		//break;
	    } // end of if (mode.fitnessCounter <= 0)
	    suggestThreshold();
	}

	void isitnotfit() {
	    // if any presynaptic neuroid has fired in this period
	    try {
		Iteration.loop(synapses, new Task() {
			public void job(Object o) throws TaskException {
			    if (((Synapse)o).isPotentiated()) 
				throw new BreakOutOfIterationException();
			}}); 
	    } catch (BreakOutOfIterationException e) {
		fitless();
	    } // end of try-catch
	}

	void fit() throws ConceptSaturatedException {
	    makeConcept(); // Throws the exception
	    state = UM; // Memorized!
	    threshold = 0.9 * suggestedThreshold; // Set threshold lower than anticipated
	    System.out.println("Into UM mode!; " + this);
	    setChanged();
	}

	/**
	 * Resets the neuroid to its initial <i>pristine</i> state.
	 * Sets the mode to AM and initializes the weights to 1.
	 * Called from step().
	 * @see #step
	 */
	void reset() {
	    //debug = true;		// Enable debugging for neuroids that're reset

	    if (debug) 
		System.out.println("Resetting " + this);
	
	    // Available memory state
	    // AM -> AM1 threshold
	    // TODO: Just set the state!
	    state = AM;
	    threshold = area.getActivationThreshold();
	    fitnessCounter = 1;
	    setChanged();

	    // Iterate over synapses and set weights to 1
	    UninterruptedIteration.loop(synapses.iterator(), new Task() {
		    public void job(Object o) {
			Synapse s = (Synapse) o;
			s.setWeight(1);
			s.weight.notifyObservers(new Double(area.time));
		    }});
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
		    sumOfCurrentWeights += weight;
		    s.weight.notifyObservers(new Double(area.time));
		}
		// TODO: needs saturation mechanism
		void potentiatedSynapse(Synapse s) {
		    s.setWeight( weight * correctTimesRequired /
				 fitnessCounter);
		}
		// TODO: needs saturation mechanism
		void silentSynapse(Synapse s) {
		    s.setWeight( weight * fitnessCounter /
				 correctTimesRequired);
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
	 * Add suggested threshold and fitnessCounter to <code>super.getStatus()</code>.
	 *
	 * @return a <code>String</code> value
	 */
	public String getStatus() {
	    return 
		super.getStatus() + 
		", fit = " + fitnessCounter +
		", sugT = " + Network.numberFormat.format(suggestedThreshold) /*+
		", sumOW = " + Network.numberFormat.format(sumOfCurrentWeights)*/;

	}


    }

}// SRMNeuroid
