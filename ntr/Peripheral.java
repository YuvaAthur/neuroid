package neuroidnet.ntr;
import neuroidnet.ntr.*;
import neuroidnet.remote.*;
import java.lang.*;
import java.util.*;
import java.io.*;
//import java.rmi.*;
import neuroidnet.utils.*;

// $Id$
/**
 *
 * <p>Controls inputs to the <code>Network</code>.
 * Has subclasses to create test networks with specific inputs.
 *
 * @see Network 
 *
 * Created: Mon Nov 20 02:08:10 2000
 *
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version $Revision$
 */
public abstract class Peripheral  implements Serializable {

    /**
     * Pointer to associated <code>Network</code> object
     * @see ntr.Network
     */
    protected Network network;

    /**
     * Peripheral system's own account of time.
     *
     */
    protected double time;

    /**
     * Creates a new <code>Peripheral</code> instance.
     * Has bidirectional interaction with the <code>Network</code>.
     *
     * @see Network#setPeripheral
     * @param network a <code>Network</code> value
     */
    public Peripheral (Network network) {
	this.network = network;
	this.time = 0;

	network.setPeripheral(this);
    }


    /**
     * Peripheral control of timepass, initiates peripheral events associated with time.
     * Delegates to <code>network.step()</code>.
     * @see Network#step
     */
    public void step() {
	eventsAtThisTime();
	network.step();
	time += network.deltaT;
    }

    /**
     * Called by step(), determines periperal actions.
     * Should be overloaded in instances of this class
     * @see #step
     */
    protected abstract void eventsAtThisTime();
    

    /**
     * An input concept represented by <code>Synapse</code>s connecting to replication factor
     * number of <code>Neuroid</code>s in the <code>Area</code>.
     * OSOLETE!!!
     * @see Area
     * @see Neuroid
     * @see Synapse
     * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
     * @version 1.0
     * @since 1.0
     */
/*
    public class Concept {
	Vector synapses;

	public Concept(Remote.AreaInt area) {
	    Synapse synapseTemplate =
		new Synapse(null, null, area.timeConstantM, network.deltaT, false, 0);
	    try {
		synapses =
		    area.addArbitrarySynapses(synapseTemplate, null, area.getReplication());
	    } catch (java.rmi.RemoteException e) {
		e.printStackTrace();
		throw new RuntimeException("Cannot access area.addArbitrarySynapses");
	    }
	    // TODO: teach the dest neuroids to memorize this input.
	}

	public Concept(ntr.Area area) {
	    Synapse synapseTemplate =
		new Synapse(null, null, area.timeConstantM, network.deltaT, false, 0);
	    synapses = area.addArbitrarySynapses(synapseTemplate, null, area.getReplication());
	}

	public void fire() {
	    Iteration.loop(synapses.iterator(), new Task() {
		    public void job(Object synapse) {
			// Induce reception of spike
			if (synapse instanceof Synapse)
			    ((Synapse)synapse).receiveSpike(); 
			else {
			    try {
				((Remote.SynapseInt)synapse).receiveSpike();
			    } catch (java.rmi.RemoteException e) { 
				e.printStackTrace();
				throw new RuntimeException("Cannot call synapse.receiveSpike");
			    }
			} // end of else
		    }});
	}
    }*/

    
}// Peripheral
