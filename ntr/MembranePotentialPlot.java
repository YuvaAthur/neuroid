
package neuroidnet.ntr;

import edu.ull.cgunay.utils.*;
import edu.ull.cgunay.utils.plots.*;

import java.util.*;

// $Id$
/**
 * MembranePotentialPlot.java
 *
 *
 * <p>Created: Tue Apr 16 14:30:39 2002
 * <p>Modified: $Date$
 *
 * @author <a href="mailto:">Cengiz Gunay</a>
 * @version $Revision$ for this file.
 */

public class MembranePotentialPlot extends Plot {

    /**
     * <code>Vector</code> of <code>WeightedPotentialPlot</code>
     * @see WeightedPotentialPlot
     */
    final Vector plots = new Vector();
    
    /**
     * Get the value of plots.
     * @return value of plots.
     */
    public Vector getPlots() {
	return plots;
    }

    /**
     * Number for generating unique identifiers for the body functions
     * created by this class in this execution.
     *
     */
    static int bodySuffix = 0;

    NeuroidProfile neuroidProfile;

    public MembranePotentialPlot (String title, Range range, NeuroidProfile neuroidProfile) {
	super(title, range);

	this.neuroidProfile = neuroidProfile;

	final Range maximalRange = new Range(0, 0);

	new UninterruptedIteration() {
	    boolean first = true;
	    public void job(Object o) {
		Plot plot = ((SynapseProfile)o).getWeightedPotentialPlot();
		plots.add(plot);

		Range plotRange = plot.getRange(); 

		if (plotRange == null) 
		    return;
		
		if (first) {
		    maximalRange.setStart(plotRange.getStart());
		    maximalRange.setEnd(plotRange.getEnd());
		    first = false;
		} else 
		    maximalRange.add(plotRange);
	    }
	}.loop(neuroidProfile.getSynapseProfiles());

	if (this.range == null) 
	    this.range = maximalRange;
    }


    /**
     * Copies the epsp() function definition from PotentialPlots,
     * defines refrac(), the refractory potential function, and then puts them
     * together as bodyXX() function to appear as the body of this plot.
     * @return a <code>String</code> value
     */
    public String preamble() {
	String[] params = { "t" }, refParams = { "t", "timeConstantR" };
	return 
	    ((Plot)plots.firstElement()).preamble(grapher) + // for epsp() func_def
	    def_func("refrac", refParams,
		     mul(geq(refParams[0], "0"),
			 neg(exp(neg(div("t", refParams[1])))))) +
	    def_func("body" + ++bodySuffix, params,
		     add(new StringTask("0") { // add synapse activity including weights
			     public void job(Object o) {
				 Plot plot = ((Plot)o);
				 
				 plot.setGrapher(grapher);
				 retval = add(retval, plot.body());
			     }
			 }.getString(plots),
			 mul(profile(neuroidProfile.getModeProfile().getThresholdProfile(),
				     range), // refractory effect multiplied by threshold profile
			     new StringTask("0") {
				 String refTCStr =
				     "" + ((SRMNeuroid)neuroidProfile.getNeuroid()).
				     getRefractoryTimeConstant();
				 public void job(Object o) {
				     double time = ((Double)o).doubleValue();
				     if (time >= range.getStart() && time <= range.getEnd()) {
					 String[] refParams =
					     { sub("t", "" + time), refTCStr };

					 retval = add(retval, func("refrac", refParams));
				     } // end of if (time >= range.getStart() && time <= range.setEnd())
				 }
			     }.getString(neuroidProfile.getNeuroid().getSpikeTrain()))));
    }

    public String body() {
	String[] params = { "t" };

	return func("body" + bodySuffix, params);
    }
    
}// MembranePotentialPlot
