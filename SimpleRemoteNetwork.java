import java.rmi.*;
import java.net.*;

/**
 * SimpleRemoteNetwork.java
 *
 *
 * Created: Fri Dec  1 03:05:53 2000
 *
 * @author Cengiz Gunay
 * @version
 */

public class SimpleRemoteNetwork extends SimpleNetwork {

    public SimpleRemoteNetwork (boolean isConcurrent) {
	super(isConcurrent);
    }

    public void build() {
	RemoteAreaInt inputArea1, inputArea2, circuitArea;
	try {
	    inputArea1 = (RemoteAreaInt) Naming.lookup("rmi://grad06.cacs/I1");
	    inputArea2 = (RemoteAreaInt) Naming.lookup("rmi://swamp08.cacs/I2");
	    circuitArea = (RemoteAreaInt) Naming.lookup("rmi://swamp04.cacs/C");
	} catch (RemoteException e) {
	    System.out.println("Error: " + e);
	    return;
	}catch (NotBoundException e) {
	    System.out.println("Error: " + e);
	    return;
	} catch (MalformedURLException e) {
	    System.out.println("Error: " + e);
	    return;
	}

	areas.add(inputArea1);
	areas.add(inputArea2);
	areas.add(circuitArea);

	try {
	    inputArea1.connectToArea(circuitArea);
	    inputArea2.connectToArea(circuitArea);
	} catch (RemoteException e) {
	    System.out.println("Remote connection to Area failed.");
	}

	peripheral =
	    new SimplePeripheral(this, inputArea1, inputArea2, circuitArea);
	
    }

    public static void main (String[] args) {
	new SimpleRemoteNetwork(true); // multi threads!
    } // end of main ()

}// SimpleRemoteNetwork
