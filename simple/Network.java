package neuroidnet.Simple;
import neuroidnet.ntr.*;
import neuroidnet.Remote.*;

/**
 * SimpleNetwork.java
 * Create a simple test network.
 *
 * Created: Thu Nov 23 03:09:39 2000
 *
 * @author Cengiz Gunay
 * @version
 */

public class Network extends ntr.Network {
    MultiConceptPeripheral peripheral;

    public Network (boolean isConcurrent) {
	super(0.01, isConcurrent);		// sets deltaT
    }

    /**
     * 
     */
    protected void build() {
	//TO DO: Implement this method.
	int
	    numberOfNeuroids = 100,
	    replication = 15;

	double period = Neuroid.defaultPeriod();

	ntr.Area inputArea1 = new ntr.Area("I1", numberOfNeuroids, replication, deltaT, period, 0.9);
	ntr.Area inputArea2 = null; //new Area("I2", numberOfNeuroids, replication, deltaT, period, 0.9);
	ntr.Area circuitArea = new ntr.Area("C", numberOfNeuroids, replication, deltaT, period, 1.8);
	areas.add(inputArea1);
	//areas.add(inputArea2);
	areas.add(circuitArea);

	inputArea1.connectToArea(circuitArea);
	//inputArea2.connectToArea(circuitArea);

	peripheral =
	    new MultiConceptPeripheral(this, inputArea1, circuitArea);
    }


    /**
     *
     */
    protected void simulation() {
	// Step 
	double untilTime = 6.0;
	long startTime = System.currentTimeMillis();
	for (int i = 0; i < untilTime/deltaT; i++) {
	    //System.out.println("STEP " + i);

	    // Fire both inputs initially
	    if (i == 0)
		peripheral.fireInputs();

	    if (i > 5 && i < 305) { 
		peripheral.fireRandomNoise();
	    } // end of if

	    // Fire noise after global inhibition is in effect, 
	    // therefore no concepts will actually fire!
	    /*if (i == 5) { 
		peripheral.fireNoise1();
	    } // end of if (i == 5*deltaT)

	    if (i == 10) { 
		peripheral.fireNoise2();
	    } // end of if (i == 5*deltaT)
	    
	    */
	    // Fire only one later
	    double alarm = i*deltaT - 5.0;
	    if (alarm > 0 && alarm < deltaT)
		peripheral.testOneInput();

	    step();		// step deltaT
	}
	System.out.println("Elapsed time: " + (System.currentTimeMillis() - startTime) + " milliseconds.");
    }


    public static void main (String[] args) {
	new Network(false); // Single threaded!
	System.exit(0);
    } // end of main ()

    
}// SimpleNetwork
