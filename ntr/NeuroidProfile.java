
package neuroidnet.ntr.plots;

import neuroidnet.ntr.*;

import java.io.*;
import java.util.*;

// $Id$
/**
 * NeuroidProfile.java
 *
 *
 * <p>Created: Tue Apr  9 15:25:36 2002
 * <p>Modified: $Date$
 *
 * @author <a href="mailto:">Cengiz Gunay</a>
 * @version $Revision$ for this file.
 */

public class NeuroidProfile implements Serializable {

    Profile modeProfile;
    
    /**
     * Get the value of modeProfile.
     * @return value of modeProfile.
     */
    public Profile getModeProfile() {
	return modeProfile;
    }
    
    /**
     * Set the value of modeProfile.
     * @param v  Value to assign to modeProfile.
     */
    public void setModeProfile(Profile  v) {
	this.modeProfile = v;
    }
    

    Vector synapseProfiles;
    Neuroid neuroid;
    public Vector spikesEmitted = new Vector();

    public NeuroidProfile (Neuroid neuroid) {
	this.neuroid = neuroid;

	// iterate on all synapses and set their watches, then take their profiles
	
	// create a new modeProfile here.
	modeProfile = new Profile(neuroid.getMode(), new Double(neuroid.getArea().time));
    }

    public SpikePlot neuroidFirePlot(Grapher grapher) {
	return new SpikePlot(grapher, null, null, spikesEmitted);
    }

}// NeuroidProfile
