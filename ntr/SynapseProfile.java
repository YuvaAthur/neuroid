
package neuroidnet.ntr;

import edu.ull.cgunay.utils.*;
import edu.ull.cgunay.plots.*;

import java.io.*;

// $Id$
/**
 * SynapseProfile.java
 *
 *
 * <p>Created: Sun Apr 14 00:10:02 2002
 * <p>Modified: $Date$
 *
 * @author <a href="mailto:">Cengiz Gunay</a>
 * @version $Revision$ for this file.
 */

public class SynapseProfile  implements Serializable {
    Profile weightProfile;
    
    /**
     * Get the value of weightProfile.
     * @return value of weightProfile.
     */
    public Profile getWeightProfile() {
	return weightProfile;
    }
    
    /**
     * Set the value of weightProfile.
     * @param v  Value to assign to weightProfile.
     */
    public void setWeightProfile(Profile  v) {
	this.weightProfile = v;
    }
    
    Synapse synapse;
    
    /**
     * Get the value of synapse.
     * @return value of synapse.
     */
    public Synapse getSynapse() {
	return synapse;
    }
    
    /**
     * Set the value of synapse.
     * @param v  Value to assign to synapse.
     */
    public void setSynapse(Synapse  v) {
	this.synapse = v;
    }

    public SynapseProfile (Synapse synapse) {
	this.synapse = synapse;

	weightProfile = new Profile(synapse.getWeightObject(),
				     new Double(synapse.destNeuroid.getArea().time));
    }

    public SpikePlot getSpikesReceivedPlot() {
	return new SpikePlot("Spikes received from " + synapse, null, synapse.getSpikeTrain());
    }

    public Plot getPotentialPlot() {
	return new PotentialPlot("Potential of " + synapse, null, this);
    }

    public Plot getWeightedPotentialPlot() {
	Neuroid src = synapse.getSrcNeuroid();
	return new WeightedPotentialPlot(synapse.nameString() +
					 " from " + src + " " + src.getConcept(), null, this);
    }

    public Plot getWeightPlot() {
	return new ProfilePlot("Weight of " + synapse, null, getWeightProfile());
    }
}
    
