
package neuroidnet.ntr;

import edu.ull.cgunay.utils.StringTask;
import edu.ull.cgunay.utils.plots.*;

import java.util.*;

// $Id$
/**
 * Plot for synapse potential.
 *
 *
 * <p>Created: Mon Apr 15 22:15:15 2002
 * <p>Modified: $Date$
 *
 * @see Synapse
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version $Revision$ for this file.
 */

public class PotentialPlot extends SimplePlot  {
    SynapseProfile synapseProfile;

    public PotentialPlot (String title, Range range, SynapseProfile synapseProfile) {
	super(title, range);

	this.synapseProfile = synapseProfile;

	try {
	    if (range == null) {
		Synapse synapse = synapseProfile.getSynapse();
		Vector spikes = synapse.getSpikeTrain();
		this.range = new Range(((Double)spikes.firstElement()).doubleValue(),
				       ((Double)spikes.lastElement()).doubleValue() +
				       synapse.getTimeConstantM() + synapse.getTimeConstantS());
	    } // end of if (range == null)

	    // expand the range with profile changes?
	    //range.add(synapseProfile.getRange());
	} catch (NoSuchElementException e) {
	    // do nothing, keep empty range
	} // end of try-catch
	
    }

    /**
     * Define the <code>epsp()</code> function here.
     *
     * @return a <code>String</code> value
     */
    public String preamble() {
	String time = sub("t","delay");
	String[] params = {"t", "timeConstantM", "timeConstantS", "delay"};
	return def_func("epsp", params,
			mul(geq(time, "0"),
			    div(sub(exp(neg(div(time, "timeConstantM"))),
				    exp(neg(div(time, "timeConstantS")))),
				sub("1", div("timeConstantS", "timeConstantM")))));
    }

    /**
     * Iterates on the spikes receive times of the synapse and adds
     * <code>epsp()</code> functions for each.
     * @see #preamble
     * @return a <code>String</code> value
     */
    public String body() {
	Synapse synapse = synapseProfile.getSynapse();
	final String
	    timeConstantM = "" + synapse.getTimeConstantM(),
	    timeConstantS = "" + synapse.getTimeConstantS(), 
	    delay =  "" + synapse.getDelay();

	// iterate on spikes in synapse and add them up
	return new StringTask("0") {
		public void job(Object o) {
		    String[] params = { sub("t", "" + o), timeConstantM,
					timeConstantS, delay};
		    retval = add(retval, func("epsp", params));
		}
	    }.getString(synapse.getSpikeTrain());
    }
    
}// PotentialPlot
