
package neuroidnet.ntr;

import neuroidnet.utils.*;
import neuroidnet.ntr.plots.*;

import java.io.*;
import java.util.*;

// $Id$
/**
 * Keeping track of changes of <code>Neuroid</code>s.
 *
 *
 * <p>Created: Tue Apr  9 15:25:36 2002
 * <p>Modified: $Date$
 *
 * @author <a href="mailto:">Cengiz Gunay</a>
 * @version $Revision$ for this file.
 */

public class NeuroidProfile implements Serializable {

    ModeProfile modeProfile;
    
    /**
     * Get the value of modeProfile.
     * @return value of modeProfile.
     */
    public ModeProfile getModeProfile() {
	return modeProfile;
    }
    
    /**
     * Set the value of modeProfile.
     * @param v  Value to assign to modeProfile.
     */
    public void setModeProfile(ModeProfile  v) {
	this.modeProfile = v;
    }

    final Vector synapseProfiles = new Vector();
    
    /**
     * Get the value of synapseProfiles.
     * @return value of synapseProfiles.
     */
    public Vector getSynapseProfiles() {
	return synapseProfiles;
    }
    
    Neuroid neuroid;
    
    /**
     * Get the value of neuroid.
     * @return value of neuroid.
     */
    public Neuroid getNeuroid() {
	return neuroid;
    }
    
    /**
     * Set the value of neuroid.
     * @param v  Value to assign to neuroid.
     */
    public void setNeuroid(Neuroid  v) {
	this.neuroid = v;
    }

    //Vector spikesEmitted = new Vector();
    
    /**
     * Get the value of spikesEmitted.
     * @return value of spikesEmitted.
     */
    public Vector getSpikesEmitted() {
	return neuroid.getSpikeTrain();
    }
    
    /**
     * Set the value of spikesEmitted.
     * @param v  Value to assign to spikesEmitted.
     */
    /*public void setSpikesEmitted(Vector  v) {
	this.spikesEmitted = v;
    }*/

    public NeuroidProfile (Neuroid neuroid) {
	this.neuroid = neuroid;

	// iterate on all synapses and set their watches, then take their profiles
	Iteration.loop(neuroid.synapses, new Task() {
		public void job(Object o) {
		    Synapse s = ((Synapse)o);
		    s.setWatch(true);
		    synapseProfiles.add(s.getProfile());
		}
	    });
	
	// create a new modeProfile here.
	modeProfile = new ModeProfile(neuroid.getMode(), new Double(neuroid.getArea().time));
    }

    public SpikePlot getSpikesEmittedPlot() {
	return new SpikePlot("Spikes emitted from " + neuroid, null, getSpikesEmitted());
    }

    public Plot getMembranePotentialPlot() {
	return new MembranePotentialPlot("Membrane potential of  " + neuroid, null, this);
    }

}// NeuroidProfile
