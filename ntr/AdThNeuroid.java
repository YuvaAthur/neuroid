
package neuroidnet.ntr;

import edu.ull.cgunay.utils.*;

// $Id$
/**
 * SRM neuroid augmented with an adaptive threshold element that takes the maximum value
 * reached by the membrane potential.
 *
 * <p>Created: Tue May  7 00:25:30 2002
 * <p>Modified: $Date$
 *
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version $Revision$ for this file.
 */
public class AdThNeuroid extends SRMNeuroid  {

    /**
     * The adaptive threshold.
     *
     */
    final AdaptiveThresholdDevice thresholdDevice;

    /**
     * Creates a threshold with infinite baseline (can never be crossed!).
     *
     * @param area an <code>Area</code> value
     */
    public AdThNeuroid (Area area) {
	super(area);

	thresholdDevice = new AdaptiveThresholdDevice();
    }

    /**
     * Creates a proper neuroid.
     *
     * @param area an <code>Area</code> value
     * @param initialThreshold <code>baseline</code> for the adaptive threshold.
     * @param refractoryTimeConstant a <code>double</code> value
     * @see AdaptiveThresholdDevice#baseline
     */
    public AdThNeuroid(Area area, double initialThreshold, double refractoryTimeConstant) {
	super(area, initialThreshold, refractoryTimeConstant);

	thresholdDevice = new AdaptiveThresholdDevice(initialThreshold);
    }
    
    /**
     * Steps the <code>threshold</code>.
     */
    public void calculatePotential() {
	super.calculatePotential();

	// Maintain the threshold here
	thresholdDevice.step();
    }

    /**
     * Describe class <code>AdThNeuroid</code> here.
     *
     */
    class AdaptiveThresholdDevice implements Simulation {
	/**
	 * Circuit parameters: r1 & c1 are fast acting component (1/10th of mTC),
	 * and r2 & c2 are slow acting component (10 times of mTC) of the threshold.
	 *
	 */
	final double r1 = 1e4, r2 = 1e5, c1 = 1e-7, c2 = 1e-6;

	/**
	 * The threshold value;
	 *
	 */
	double value;
	
	/**
	 * Get the value of value.
	 * @return value of value.
	 */
	public double getValue() {
	    return value;
	}
	
	/**
	 * Set the value of value.
	 * @param v  Value to assign to value.
	 */
	public void setValue(double  v) {
	    this.value = v;
	}

	/**
	 * The last time the threshold is crossed by the membrane potential.
	 *
	 */
	double crossTime;

	/**
	 * The value of membrane potential at <code>crossTime</code> 
	 * (i.e. max value).
	 *
	 */
	double maxPotential;

	/**
	 * The initial  dictated by the area forming a threshold baseline thereafter.
	 *
	 */
	final double baseline;

	/**
	 * Sets the <code>baseline</code>.
	 *
	 * @param baseline a <code>double</code> value
	 */
	public AdaptiveThresholdDevice(double baseline) {
	    this.baseline = baseline;
	    maxPotential = baseline;
	}

	/**
	 * Dummy constructor, sets the <code>baseline</code> to
	 * infinity.
	 *
	 */
	public AdaptiveThresholdDevice() {
	    baseline = Double.MAX_VALUE;
	}

	/**
	 * Dummy.
	 */
	public void init() {}

	/**
	 * Returns current value of threshold according to
	 * <code>crossTime</code> and <code>maxPotential</code>.
	 *
	 * @return a <code>double</code> value
	 */
	double kernel() {
	    double k2 = (maxPotential - baseline) * r2 / (r1 + r2);
	    double k3 = maxPotential - baseline - k2;

	    double v2 = k2 * Math.exp(-(area.time - crossTime) / (c2 * r2)) + baseline;
	    double v3 = k3 * Math.exp(-(area.time - crossTime) / (c1 * r1)) + v2;

	    return v3;
	}

	/**
	 * If membrane <code>potential</code> crosses the threshold
	 * <code>value</code>, then assign it to
	 * <code>maxPotential</code> and set <code>crossTime</code> to
	 * now.
	 * Update the threshold <code>value</code> by calling <code>kernel()</code>.
	 * @see #kernel
	 * @see Neuroid#potential
	 * @see #maxPotential
	 * @see #crossTime
	 * @see #value
	 */
	public void step() {
	    // TODO: here potential should be compared with maxPotential and in Mode.step()
	    // it should be compared with value for firing.
	    if (potential > value) {
		maxPotential = potential;
		crossTime = area.time;
	    } // end of if (potential > value)
	    
	    value = kernel();
	}
    }

    /**
     * Finite state part of the neuroid.
     *
     */
    protected class Mode extends SRMNeuroid.Mode {
	
	State state;

	final State quiescent = new State() {
		public void init() { name = "Q"; };
		public void step() {
		    threshold = thresholdDevice.getValue(); // How about sugT?
		    if (potential > threshold) {
			state = rising;
		    } // end of if 
		    
		}
	    };

	final State rising = new State() {
		public void init() { name = "R"; }
		public void step() {
		    suggestedThreshold = thresholdDevice.getValue();
		    if (potential < suggestedThreshold) {
			state = quiescent;
			threshold = 0;
			Mode.super.step(); // Call SRMNeuroid.Mode.step
			fire();
		    } // end of if 
		    
		}
	    };

	/**
	 * Initalizes <code>state</code> to <code>quiescent</code>.
	 * @see #state
	 * @see #quiescent
	 */
	Mode() {
	    state = quiescent;
	}

	/**
	 * Calls <code>state.step()</code>.
	 * @see State#step
	 */
	public void step() {
	    state.step();
	}
    }
}// AdThNeuroid
