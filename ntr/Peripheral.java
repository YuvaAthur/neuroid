package Base;
import Base.*;
import Remote.*;
import java.lang.*;
import java.util.*;
//import java.rmi.*;
import Utils.*;
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
 * @author Cengiz Gunay
 * @version $Revision$$
 */

public class Peripheral  {
    Network network;

    /**
     * Creates a new <code>Peripheral</code> instance.
     * Has bidirectional interaction with the <code>Network</code>.
     *
     * @see Network#setPeripheral
     * @param network a <code>Network</code> value
     */
    public Peripheral (Network network) {
	this.network = network;

	network.setPeripheral(this);
    }

    /**
     * An input concept represented by <code>Synapse</code>s connecting to replication factor
     * number of <code>Neuroid</code>s in the <code>Area</code>.
     *
     * @see Area
     * @see Neuroid
     * @see Synapse
     * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
     * @version 1.0
     * @since 1.0
     */
    public class Concept {
	/**
	 * Vector of <code>Synapses</code> connected to <code>Neuroids</code>
	 * that this <code>Concept</code> should activate.
	 */
	Vector synapses;

	public Concept(Remote.AreaInt area) {
	    try {
		synapses = area.createArbitrarySynapses(area.getReplication());
	    } catch (java.rmi.RemoteException e) {
		e.printStackTrace();
		throw new RuntimeException("Cannot access area.createArbitrarySynapses");
	    }
	    // TODO: teach the dest neuroids to memorize this input.
	}

	public Concept(Base.Area area) {
	    synapses = area.createArbitrarySynapses(area.getReplication());
	}

	public void fire() {
	    Iteration.loop(synapses.iterator(), new Utils.Task() {
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
    }

    
}// Peripheral