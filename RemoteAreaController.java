import java.rmi.*;
/**
 * RemoteAreaController.java
 * Create a RemoteArea and register it to the Registry.
 * @see RemoteArea
 * Created: Fri Dec  1 02:56:09 2000
 *
 * @author Cengiz Gunay
 * @version
 */

public class RemoteAreaController  {
    public RemoteAreaController () {
	
    }

    /**
     * Describe <code>main</code> method here.
     * Get parameters from command line and start an area:
     * - name of area
     * - SRM params 
     * @param args a <code>String[]</code> value
     */
    public static void main (String[] args) {
	if (args.length < 2) {
	    System.out.println("Usage: RemoteAreaController name threshold");
	    return;
	}
	String name = args[0];
	double threshold = Double.parseDouble(args[1]);
	int
	    numberOfNeuroids = 500,
	    replication = 5;

	double deltaT = 0.01;
	double period = Neuroid.defaultPeriod();
	
	System.setSecurityManager(new RMISecurityManager());
	try {
	    RemoteArea area =
		new RemoteArea(name, numberOfNeuroids, replication, deltaT, period, threshold);
	    Naming.bind(name, area);
	    System.out.println("Bound to rmiregistry");
	    
	} catch (Exception e) {
	    System.out.println("Error: " + e);
	}

	
    } // end of main ()
    
}// RemoteAreaController
