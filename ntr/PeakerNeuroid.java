
package neuroidnet.ntr;

// $Id$
/**
 * Intermediate neuroid augmenting <code>SRMNeuroid</code> before
 * proceeding into a more completely liquid-state machine neuroid.
 * 
 * <p><code>PeakerNeuroid</code> simply transforms from the
 * continuous state operation of the SRM to Valiant's abstraction.
 * This is done by invoking the underlying SRMNeuroid (apparently
 * mistakenly built following Valiant's approach too closely) finite
 * state machine whenever the membrane potential has a max plateau.
 * The intention here is to make the peaks in the membrane potential of the
 * continuous model correspond to the sample times of the discrete model.
 *
 * <p>State: works
 * <p>Todo: There is no threshold check which makes it susceptible to criticism.
 * <p>Created: Tue May 14 23:39:07 2002
 * <p>Modified: $Date$
 *
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version $Revision$ for this file.
 */

public class PeakerNeuroid extends SRMNeuroid  {

    double dPotential;

    double dPotentialOld;

    double ddPotential;
    double dddPotential;

    /**
     * TODO: Initialize mode? Does the polymorphism work from super
     * class to use Mode defined here? [No]
     */
    public PeakerNeuroid (Area area) {
	super(area);

	setMode(new Mode());
    }

    public PeakerNeuroid(Area area, double initialThreshold, double refractoryTimeConstant) {
	super(area, initialThreshold, refractoryTimeConstant);

	setMode(new Mode(initialThreshold));
    }
    
    /**
     * Calculates up to the third derivative of the membrane potential
     * to detect max plateaus.
     * <p>TODO: Call super.step()? Make super.step() compatible with this first.
     * <p>TODO: try to unify with neuroid.step
     */
    public void step() {
	double potential = this.potential;

	calculatePotential();

	dPotentialOld = dPotential;

	// Calculate first three derivatives of the membrane potential
	double dPotential = this.potential - potential;
	double ddPotential = dPotential - this.dPotential;
	this.dPotential = dPotential;
	double dddPotential = ddPotential - this.ddPotential;
	this.ddPotential = ddPotential;
	this.dddPotential = dddPotential;

	mode.step();
    }

    //static double minDPotential = 0.1;

    protected class Mode extends SRMNeuroid.Mode {

	
	/**
	 * The state for the continuous-time state machine. The name
	 * is chosen to be distinct from dicrete-time state machine.
	 */
	State _state;

	final State quiescent = new State("Q") {
		public void step() {
		    if (dPotential > 0) {
			_state = rising;
			if (debug) 
			  System.out.println("t=" + area.time + ", " +
					   PeakerNeuroid.this + ": Rising");   
			setChanged();
		    }
		}
	    };

	final State rising = new State("R") {
		public void step() {
		    if (debug) {
			System.out.println("t=" + area.time + ", " + PeakerNeuroid.this +
					   ": p=" + potential +
					   ", dP=" + dPotential +
					   ", _dP=" + dPotentialOld + 
					   ", ddP=" + ddPotential + ", dddP=" + dddPotential);
		    } // end of if (debug)

		    // if peak condition is met call Mode.super.step();		    
		    // When derivative is small enough (we can also check for the zero crossing)
		    //if (dPotential < 0.1 && ddPotential < 0 && dddPotential <= 0.1) {
		    if (dPotentialOld > 0 && dPotential <= 0) {
			Mode.super.step(); // Discrete step
			_state = plateau;
			if (debug) 
			    System.out.println("t=" + area.time + ", Plateau: " +
					       PeakerNeuroid.this.getStatus());   
			setChanged();
		    } // end of if 
		}
	    };

	final State plateau = new State("P") {
		public void step() {
		    if (dPotential < 0) {
			_state = quiescent;
			setChanged(); 
		    } else if (dPotential > 0) {
			_state = rising;
			setChanged();
		    } 
		    
		}
	    };

	Mode(double threshold) {
	    super(threshold);
	    _state = quiescent;
	}

	Mode() {
	    super();
	    _state = quiescent;
	}

	public void step() {
	    _state.step();

	    notifyObservers(new Double(area.time));
	}

	public String getStatus() {
	    return super.getStatus() + ", " + _state;
	}
    }
}// PeakerNeuroid
