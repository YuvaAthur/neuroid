import java.rmi.*;
import java.rmi.server.*;

/**
 * RemoteArea.java
 *
 *
 * Created: Fri Dec  1 02:54:41 2000
 *
 * @author Cengiz Gunay
 * @version
 */

public class RemoteArea extends Area
    implements RemoteAreaInt {
    public RemoteArea (String name, int numberOfNeuroids, int replication,
		       double deltaT, double period, double threshold) throws RemoteException {
	super(name, numberOfNeuroids, replication, deltaT, period, threshold);
	UnicastRemoteObject.exportObject(this);
    }

    /**
     * Overload method in <code>Area</code> to create <code>RemoteSynapse</code> instead.
     * @see Area#createRandomSynapse
     * @return a <code>Synapse</code> value
     */
    public Synapse createRandomSynapse() {
	try {
	    Synapse s = new RemoteSynapse(getRandomNeuroid(), 1, deltaT, false);
	    //System.out.println("***synapse: " + s);
	    return s;
	} catch (RemoteException e) {
	    e.printStackTrace();
	    throw new RuntimeException("Cannot instantiate RemoteSynapse!");
	}
    }

    /*public void connectToArea(RemoteArea destArea) throws RemoteException {
	
    } */   
}// RemoteArea
