
/**
 * SimpleNetwork.java
 * Create a simple test network.
 *
 * Created: Thu Nov 23 03:09:39 2000
 *
 * @author Cengiz Gunay
 * @version
 */

public class SimpleNetwork extends Network {
    SimplePeripheral peripheral;

    public SimpleNetwork (boolean isConcurrent) {
	super(0.01, isConcurrent);		// sets deltaT
    }

    /**
     * 
     */
    public void build() {
	//TO DO: Implement this method.
	int
	    numberOfNeuroids = 750,
	    replication = 5;

	double period = Neuroid.defaultPeriod();

	Area inputArea1 = new Area("I1", numberOfNeuroids, replication, deltaT, period, 0.9);
	Area inputArea2 = new Area("I2", numberOfNeuroids, replication, deltaT, period, 0.9);
	Area circuitArea = new Area("C", numberOfNeuroids, replication, deltaT, period, 1.8);
	areas.add(inputArea1);
	areas.add(inputArea2);
	areas.add(circuitArea);

	//inputArea1.connectToArea(inputArea2);
	inputArea1.connectToArea(circuitArea);
	inputArea2.connectToArea(circuitArea);

	peripheral =
	    new SimplePeripheral(this, inputArea1, inputArea2, circuitArea);
    }


    /**
     *
     */
    public void simulation() {
	// Step 
	double untilTime = 6.0;
	long startTime = System.currentTimeMillis();
	for (int i = 0; i < untilTime/deltaT; i++) {
	    //System.out.println("STEP " + i);

	    // Fire both inputs initially
	    if (i == 0)
		peripheral.fireInputs();

	    // Fire only one later
	    double alarm = i*deltaT - 5.0;
	    if (alarm > 0 && alarm < deltaT)
		peripheral.testOneInput();

	    step();		// step deltaT
	}
	System.out.println("Elapsed time: " + (System.currentTimeMillis() - startTime) + " milliseconds.");
    }


    public static void main (String[] args) {
	new SimpleNetwork(false); // Single threaded!
	System.exit(0);
    } // end of main ()

    
}// SimpleNetwork
