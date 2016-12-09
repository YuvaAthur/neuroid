package neuroidnet.remote;

import neuroidnet.ntr.*;

// $Id$
/**
 * Create an <code>Area</code> and register it to the RMI registry.
 * <p>Created: Fri Dec  1 02:56:09 2000
 * TODO:
 * <ul>
 *   <li> Connect to a server and wait for a request to create an Area.
 * </ul>
 *
 * @see neuroidnet.ntr.Area
 * @author Cengiz Gunay
 * @version $Revision$
 */

public class AreaController  {
    public AreaController () {
	
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
	    System.out.println("Usage: remote.AreaController name threshold");
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
	    neuroidnet.ntr.Area area =
		new neuroidnet.ntr.Area(name, numberOfNeuroids, replication, deltaT,
					period, threshold);
	    Naming.bind(name, area);
	    System.out.println("Bound to rmiregistry");
	    
	} catch (Exception e) {
	    System.out.println("Error: " + e);
	}
	
    } // end of main ()
    
}// remote.AreaController
