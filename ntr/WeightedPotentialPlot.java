
package neuroidnet.ntr;

import edu.ull.cgunay.utils.plots.*;

// $Id$
/**
 * Weighted potential plot of a synapse.
 *
 *
 * <p>Created: Tue Apr 16 14:15:32 2002
 * <p>Modified: $Date$
 *
 * @author <a href="mailto:">Cengiz Gunay</a>
 * @version $Revision$ for this file.
 */

public class WeightedPotentialPlot extends PotentialPlot  {
    public WeightedPotentialPlot (String title, Range range, SynapseProfile synapseProfile) {
	super(title, range, synapseProfile);

	if (this.range == null) 
	    // get range from the profile
	    this.range = synapseProfile.getWeightProfile().getRange();
	//TODO: otherwise add range from profile?
    }

    public String body() {
	return mul(profile(synapseProfile.getWeightProfile(), range), super.body());
    }
    
}// WeightedPotentialPlot
