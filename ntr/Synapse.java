package neuroidnet.ntr;

import edu.ull.cgunay.utils.plots.*;
import edu.ull.cgunay.utils.*;
import neuroidnet.remote.*;
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

public class Synapse implements SynapseInt, DumpsData, Serializable, Expressive {
    /**
     * List of times when spikes are received
     */
    final Vector spikeTrain = new Vector(); // TODO: make time a class?
    
    /**
     * Get the value of spikeTrain.
     * @return value of spikeTrain.
     */
    public Vector getSpikeTrain() {
	return spikeTrain;
    }

    /**
     * Identifier number of this synapse as index in the list of synapses
     * at the postsynaptic neuroid, therefore in reference to that neuroid.
     * @see toString
     */
    int id;
    
    /**
     * Get the value of id.
     * @return value of id.
     */
    public int getId() {
	return id;
    }
    
    /**
     * Set the value of id.
     * @param v  Value to assign to id.
     */
    public void setId(int  v) {
	this.id = v;
    }
    
    
    boolean isInhibitory;

    /**
     * Synapse constant dynamic properties. The membrane time constant of the postsynaptic neuron. (Why in Snapse? See constructor documentation.)
     * @see #Synapse
     */
    double timeConstantM;
    
    /**
     * Get the value of timeConstantM.
     * @return value of timeConstantM.
     */
    public double getTimeConstantM() {
	return timeConstantM;
    }
    
    /**
     * Set the value of timeConstantM.
     * @param v  Value to assign to timeConstantM.
     */
    public void setTimeConstantM(double  v) {
	this.timeConstantM = v;
    }
    
    /**
     * Synapse constant dynamic properties. The synaptic rise time constant.
     */
    double timeConstantS;
    
    /**
     * Get the value of timeConstantS.
     * @return value of timeConstantS.
     */
    public double getTimeConstantS() {
	return timeConstantS;
    }
    
    /**
     * Set the value of timeConstantS.
     * @param v  Value to assign to timeConstantS.
     */
    public void setTimeConstantS(double  v) {
	this.timeConstantS = v;
    }
    
    /**
     * Axonal delay associated with this synapse (simplified). 
     * <code>delay</code> is required for the <code>phasesegregator.Network</code>
     * @see phasesegregator.Network
     */
    double delay;
    
    /**
     * Get the value of delay.
     * @return value of delay.
     */
    public double getDelay() {
	return delay;
    }
    
    /**
     * Set the value of delay.
     * @param v  Value to assign to delay.
     */
    public void setDelay(double  v) {
	this.delay = v;
    }

    /**
     * Incoming weight.
     */
    public Weight weight = new Weight(); 

    class Weight extends Profilable implements Serializable {
	double value;

	/**
	 * Get the value.
	 * @return value
	 */
	public double getValue() {return value;}
    
	/**
	 * Set the value of weight. Maximum allowed value is determined by
	 * constant <code>maxWeight</code>.
	 * @param v  Value to assign to weight.
	 * @see #maxWeight
	 */
	public void setValue(double  v) {
	    this.value = Math.min(maxWeight, v);
	    setChanged();		// Observable element has changed
	}

	public double doubleValue() { return getValue(); }
    }

    /**
     * Returns a reference to the <code>weight</code> object.
     *
     * @return a <code>Weight</code> value
     */
    public Weight getWeightObject() {
	return weight;
    }

    /**
     * Get the value from <code>weight</code>.
     * @see #weight
     * @return value of weight.
     */
    public double getWeight() {
	return weight.getValue();
    }
    
    /**
     * Set the value of <code>weight</code>.
     * @see #weight
     * @param v  Value to assign to weight.
     */
    public void setWeight(double  v) {
	this.weight.setValue(v);
    }
    
    /**
     * Presynaptic neuroid.
     * TODO:	The remote interfaces for this connection is not done yet.
     *		Presynaptic link can be made to either the neuroid itself or its axon, 
     *		through which the neuron can be reached.
     */
    Neuroid srcNeuroid;

    /**
     * Get the value of srcNeuroid.
     * @return value of srcNeuroid.
     */
    public Neuroid getSrcNeuroid() {
	return srcNeuroid;
    }
    
    /**
     * Set the value of srcNeuroid.
     * @param v  Value to assign to srcNeuroid.
     */
    public void setSrcNeuroid(Neuroid  v) {
	this.srcNeuroid = v;
    }
    

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

    /**
     * Constant <code>maxWeight</code> is the upper limit any synapse weight can have.
     * @see #setWeight
     */
    public final static double maxWeight = 5;

    SynapseProfile profile;
    
    /**
     * Get the value of profile.
     * @return value of profile.
     */
    public SynapseProfile getProfile() {
	return profile;
    }
    
    /**
     * Set the value of profile.
     * @param v  Value to assign to profile.
     */
    public void setProfile(SynapseProfile  v) {
	this.profile = v;
    }
    

    /**
     * If set, it saves information about its (weight) changes.
     */
    boolean watch = false;
    
    /**
     * Get the value of watch.
     * @return value of watch.
     */
    public boolean isWatch() {
	return watch;
    }
    
    /**
     * Set the value of watch.
     * @param v  Value to assign to watch.
     */
    public void setWatch(boolean  v) {
	this.watch = v;
	if (watch) 
	    profile = new SynapseProfile(this);
    }
    

    // TODO: Make subclasses where excitory/inhib or phase/period behavior can be specified
    /**
     * Creates a new <code>Synapse</code> instance. Attaches itself to
     * <code>destNeuroid</code>.  Default weight is 1.  
     * One can create a synapse template for using later by giving <code>null</code> for the
     * <code>destneuroid</code>.
     * <p>TODO: timeConstantM might need to be associated with the
     * neuroid, but then we lose the flexibility of assigning synapses
     * with different time constants. In "Brief history of time
     * constants" by C. Koch et al. it is claimed that synaptic
     * effects change according to location of synapse on dendridic
     * arbor.
     * @see Neuroid#synapses
     * @param destNeuroid a <code>Neuroid</code> value
     * @param timeConstantM a <code>double</code> value
     * @param timeConstantS a <code>double</code> value
     */
    public Synapse(Neuroid srcNeuroid, Neuroid destNeuroid, double timeConstantM,
		   double timeConstantS, boolean isInhibitory, double delay) {
	this.srcNeuroid = srcNeuroid;
	this.destNeuroid = destNeuroid;
	this.timeConstantM = timeConstantM;
	this.timeConstantS = timeConstantS;
	this.isInhibitory = isInhibitory;
	// Axonal + synaptic delay (not including integration time! See Neuroid#step)
	this.delay = delay; 

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
	this.delay = templateSynapse.delay;

	init();
    }

    /**
     * Initialization code called from various constructors.
     * Init weights and adds <code>Synapse</code> to <code>destNeuroid.synapses</code>
     * unless it's a template synapse.
     * @see ntr.Neuroid#synapses
     */
    final private void init() {
	setWeight(1.0);
// 	timeConstantM = 1;
// 	timeConstantS = destNeuroid.area.network.deltaT;
	if (destNeuroid != null) // not template synapse
	    id = destNeuroid.addSynapse(this);
    }

    /**
     * Receive a spike at this time instant. Called by the presynaptic <code>Neuroid</code>.
     * Remove old spikes from <code>spikeTrain</code> unless under watch.
     * @see Neuroid#fire
     * @see #watch
     */
    public void receiveSpike() {
	/*System.out.println("Received spike at " + this.getProperties() +
			   " connected to " + destNeuroid);*/
	double time = destNeuroid.area.time;
	// Add new spike at this time
	spikeTrain.add(new Double(time));
	
	// If the destneuroid isn't being watched, forget spikes received earlier
	// than 3 timeConstantMs ago. TODO: or maybe this synapse should be watched separately?
	if (!destNeuroid.watch) {
	    for (Iterator i = spikeTrain.iterator(); i.hasNext();) {
		if (((Double)i.next()).doubleValue() < (time - 3*timeConstantM))
		    i.remove();	// Delete expired spikes from start
		else 
		    break;
	    } // end of for (Iterator i = spikeTrain.iterator(); i.hasNext();)
	} // end of if (!destNeuroid.debug)
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
	while (true) {
	    try {
		potential = 0;
		for (Iterator i = spikeTrain.iterator(); i.hasNext(); ) {
		    double spikeTime = ((Double) i.next()).doubleValue();
		    potential += (isInhibitory?-1:1) * kernel(destNeuroid.area.time - spikeTime);
		} // end of for (iterator i = spikeTrain.iterator(); i.hasNext(); )
		break;		// out of while
	    } catch (ConcurrentModificationException e) {
		// do nothing, i.e. restart
		System.out.println("Concurrent modification in Synapse.getPotential(), repeating...");
	    }	     
	} // end of while (true)
	
	return getWeight() * potential;
    }

    /**
     * Returns true if Synapse received a spike a short time back.
     *
     * @return <code>true</code> if potential before multiplication with weight is
     * above an arbitrary value (0.1)
     */
    boolean isPotentiated() {
	return ( getPotential()/getWeight() > 0.1);
    }

    /**
     * Identifies the synapse using <code>id</code> and <code>destNeuroid</code>.
     * @return a <code>String</code> value
     */
    public String toString() {
	return "Synapse #" + id + " of " + destNeuroid;
    }

    /**
     * Include output of <code>toString()</code> plus potential and weight.
     * <p>TODO: get the potential from a variable instead of calling getPotential
     *
     * @return a <code>String</code> value
     */
    public String getStatus() {
	return
	    this + " with p=" + Network.numberFormat.format(getPotential()) +
	    ", w=" + (isInhibitory?"-":"") + Network.numberFormat.format(getWeight());
    }
    
    /**
     * Describe in higher detail.
     *
     * @return a <code>String</code> value
     */
    public String getProperties() {
	return this.getStatus() +
	    ", delay=" + Network.numberFormat.format(delay) + 
	    ", tau_m=" + timeConstantM +
	    ", tau_s=" + timeConstantS;
    }

    /**
     * Dump synaptic activity of concepts contained to output (matlab file?).
     * @deprecated Use plots instead.
     */
    public String dumpData() {
	while (true) {
	    try {
		return new StringTask() {
			int number = 0;
		
			public void job(Object o) {
			    number++;
			    super.job(o);
			    this.retval += " "; // spike times separated by space
			    if ((number % 5) == 0) // limit 5 elements per row for text file 
				super.job("...\n");
			}
		    }.getString(spikeTrain);	
	    } catch (ConcurrentModificationException e) {
		// do nothing, i.e. restart
		System.out.println("Concurrent modification in Synapse.dumpData(), repeating...");
	    }	     
	} // end of while (true)
    }
}
