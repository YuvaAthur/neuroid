package Base;
import Base.*;
import Remote.*;
import java.io.*;
import java.lang.*;
import java.util.*;
// $Id$ 
/** 
 * <p>Presynaptic connection to a Neuroid.
 * @see Neuroid
 *
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version $Revision$
 * @since 1.0
 */

public class Synapse {
    /**
     * List of times when spikes are received
     */
    Vector spikeTrain; // TODO: make time a class?
    boolean isInhibitory;

    /**
     * Synapse constant dynamic properties.
     * Delay is currently not used due to the existence of natural delay between areas. (?)
     * TODO: <code>delay</code> is required for the <code>PhaseSegregator.Network</code>
     */
    double
	timeConstantM,
	timeConstantS,
	delay; /** delay is not implemented in the parallel version */

    /**
     * Incoming weight.
     */
    double weight;

    /**
     * Presynaptic neuroid.
     * TODO:	The remote interfaces for this connection is not done yet.
     *		Presynaptic link can be made to either the neuroid itself or its axon, 
     *		through which the neuron can be reached.
     */
    Neuroid srcNeuroid;

    /**
     * Postsynaptic neuroid
     */
    Neuroid destNeuroid;
    
    /**
       * Get the value of destNeuroid.
       * @return value of destNeuroid.
       */
    public Neuroid getDestNeuroid() {return destNeuroid;}
    
    /**
       * Set the value of destNeuroid.
       * @param v  Value to assign to destNeuroid.
       */
    public void setDestNeuroid(Neuroid  v) {this.destNeuroid = v;}
    

    // TODO: Make subclasses where excitory/inhib or phase/period behavior can be specified
    /**
     * Creates a new <code>Synapse</code> instance. Attaches itself to <code>destNeuroid</code>.
     * Default weight is 1.
     * @see Neuroid#synapses
     * @param destNeuroid a <code>Neuroid</code> value
     * @param timeConstantM a <code>double</code> value
     * @param timeConstantS a <code>double</code> value
     */
    public Synapse(Neuroid srcNeuroid, Neuroid destNeuroid, double timeConstantM,
		   double timeConstantS, boolean isInhibitory) {
	this.srcNeuroid = srcNeuroid;
	this.destNeuroid = destNeuroid;
	this.timeConstantM = timeConstantM;
	this.timeConstantS = timeConstantS;
	this.isInhibitory = isInhibitory;

	init();
    }

    /**
     * Creates a new <code>Synapse</code> instance with help of a <code>templateSynapse</code>.
     * Attaches itself to <code>destNeuroid</code>
     * @see Neuroid#synapses
     * @param destNeuroid a <code>Neuroid</code> value
     * @param templateSynapse a <code>Synapse</code> value
     */
    public Synapse(Neuroid srcNeuroid, Neuroid destNeuroid, Synapse templateSynapse) {
	this.srcNeuroid = srcNeuroid;
	this.destNeuroid = destNeuroid;
	this.timeConstantM = templateSynapse.timeConstantM;
	this.timeConstantS = templateSynapse.timeConstantS;
	this.isInhibitory = templateSynapse.isInhibitory;

	init();
    }

    /**
     * Initialization code called from various contructors.
     * Init weights and adds <code>Synapse</code> to <code>destNeuroid.synapses</code>
     */
    final private void init() {
	weight = 1;
// 	timeConstantM = 1;
// 	timeConstantS = destNeuroid.area.network.deltaT;
	spikeTrain = new Vector();
	if (destNeuroid != null) // not template synapse
	    destNeuroid.synapses.add(this);
    }

    /**
     * Receive a spike at this time instant, called by the presynaptic <code>Neuroid</code>.
     * TODO: don't remove old spikes, rather just disregard them in the potential calculation.
     * @see Neuroid#fire
     */
    public void receiveSpike() {
	System.out.println("Received spike at " + this);
	
	spikeTrain.add(new Double(destNeuroid.area.time));
	
	for (Iterator i = spikeTrain.iterator(); i.hasNext();) {
	    if (((Double)i.next()).doubleValue() < (destNeuroid.area.time - timeConstantM))
		i.remove();	// Delete expired spikes from start
	    else 
		break;
	    
	} // end of for (Iterator i = spikeTrain.iterator(); i.hasNext();)
    }
    
    /**
     * Synapse effect kernel function taken from "Pulsed neural networks" of
     * Maass and Bishop 1999, Eq 1.49, p.31
     * for equivalence to the integrate and fire model and its period calculation.
     *
     * @param time a <code>double</code> value.
     * @return Kernel potential function value for given time since spike.
     */
    double kernel(double time) {
	if (time < delay) {
	    return 0;
	} else {
	    return 
		(Math.exp(-(time - delay)/timeConstantM) -
		 Math.exp(-(time - delay)/timeConstantS)) /
		(1 - (timeConstantS/timeConstantM));
	} // end of if (time < delay)
    }

    /**
     * Total effect of potentiation from Synapse.
     *
     * @return Potential of this synapse to be added to the total membrane potential of the neuroid.
     */
    double getPotential() {
	double potential = 0;
	for (Iterator i = spikeTrain.iterator(); i.hasNext(); ) {
	    double spikeTime = ((Double) i.next()).doubleValue();
	    potential += (isInhibitory?-1:1) * kernel(destNeuroid.area.time - spikeTime);
	} // end of for (iterator i = spikeTrain.iterator(); i.hasNext(); )
	return weight * potential;
    }

    /**
     * Returns true if Synapse received a spike a short time back.
     *
     * @return <code>true</code> if potential before multiplication with weight is
     * above an arbitrary value (0.5)
     */
    boolean isPotentiated() {
	return ( getPotential()/weight > 0.5);
    }

    /**
     * Method inherited from java.lang.Object to display text about <code>Synapse</code>.
     * TODO: getthe potential from a variable instead of calling getPotential
     * @return a <code>String</code> value
     */
    public String toString() {
	return "Synapse: p=" + Network.numberFormat.format(getPotential()) +
	    ", w=" + (isInhibitory?"-":"") + Network.numberFormat.format(weight) +
	    " connected to " + destNeuroid;
    }
}
