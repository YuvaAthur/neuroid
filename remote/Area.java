package neuroidnet.remote;

import neuroidnet.ntr.*;

import java.rmi.server.*;

// $Id$
/**
 * 
 *
 *
 * Created: Fri Dec  1 02:54:41 2000
 *
 * @author Cengiz Gunay
 * @version
 * @deprecated See <code>neuroidnet.ntr.Area</code>.
 */

public class Area extends neuroidnet.ntr.Area {
    public Area (String name, int numberOfNeuroids, int replication,
		       double deltaT, double period, double threshold) throws java.rmi.RemoteException {
	super(name, numberOfNeuroids, replication, deltaT, period, threshold);
	UnicastRemoteObject.exportObject(this);
    }

    /**
     * Overload method in <code>Area</code> to create <code>Remote.Synapse</code> instead.
     * TODO: fix; original method moved to AxonArbor.
     * @see Area#addRandomSynapse
     * @return a <code>Synapse</code> value
     */
    public Synapse addRandomSynapse() {
	try {
	    Synapse s = new Synapse(getRandomNeuroid(), 1, deltaT, false);
	    //System.out.println("***synapse: " + s);
	    return s;
	} catch (java.rmi.RemoteException e) {
	    e.printStackTrace();
	    throw new RuntimeException("Cannot instantiate remote Synapse!");
	}
    }

    /*public void connectToArea(remote.Area destArea) throws java.rmi.RemoteException {
	
    } */   
}// remote.Area
