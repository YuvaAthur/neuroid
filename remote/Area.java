package Remote;
import Base.*;
import Remote.*;
//import java.rmi.*;
import java.rmi.server.*;

/**
 * Remote.Area.java
 *
 *
 * Created: Fri Dec  1 02:54:41 2000
 *
 * @author Cengiz Gunay
 * @version
 */

public class Area extends Base.Area
    implements Remote.AreaInt {
    public Area (String name, int numberOfNeuroids, int replication,
		       double deltaT, double period, double threshold) throws java.rmi.RemoteException {
	super(name, numberOfNeuroids, replication, deltaT, period, threshold);
	UnicastRemoteObject.exportObject(this);
    }

    /**
     * Overload method in <code>Area</code> to create <code>Remote.Synapse</code> instead.
     * TODO: fix; original method moved to AxonArbor.
     * @see Area#createRandomSynapse
     * @return a <code>Synapse</code> value
     */
    public Synapse createRandomSynapse() {
	try {
	    Synapse s = new Remote.Synapse(getRandomNeuroid(), 1, deltaT, false);
	    //System.out.println("***synapse: " + s);
	    return s;
	} catch (java.rmi.RemoteException e) {
	    e.printStackTrace();
	    throw new RuntimeException("Cannot instantiate Remote.Synapse!");
	}
    }

    /*public void connectToArea(Remote.Area destArea) throws java.rmi.RemoteException {
	
    } */   
}// Remote.Area
