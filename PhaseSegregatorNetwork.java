
/**
 * SimpleNetwork.java
 * Create a simple test network.
 *
 * Created: Thu Nov 23 03:09:39 2000
 *
 * @author Cengiz Gunay
 * @version
 */

public class  PhaseSegregatorNetwork extends Network {
    PhaseSegregatorPeripheral peripheral;

    public PhaseSegregatorNetwork (boolean isConcurrent) {
	super(0.01, isConcurrent);		// sets deltaT
    }

    /**
     * 
     */
    public void build() {
	//TO DO: Implement this method.
	int
	    numberOfNeuroids = 100,
	    replication = 5;

	double period = Neuroid.defaultPeriod();
	int numberOfMedialAreas = 3;
	int numberOfItems = 3 * numberOfMedialAreas;

	Area inputArea = new Area("I", numberOfItems*replication, replication,
				  deltaT, period, 0.9);
	areas.add(inputArea);

	Area[] inputAreas = new Area[numberOfMedialAreas];
	for (int medialArea = 0; medialArea < numberOfMedialAreas; medialArea++) {
	     inputAreas[medialArea] =
		 new Area("I"+(medialArea+1), numberOfNeuroids, replication, deltaT, period, 0.9);
	     areas.add(inputAreas[medialArea]);
	} // end of for (int medialArea = 0; medialArea < numberOfMedialAreas; medialArea++)

	Area[] medialAreas = new Area[numberOfMedialAreas];
	Area previous = inputArea;
	for (int medialArea = 0; medialArea < numberOfMedialAreas; medialArea++) {
	     medialAreas[medialArea] =
		 new Area("M"+(medialArea+1), numberOfNeuroids, replication, deltaT, period, 2*0.9);
	     areas.add(medialAreas[medialArea]);
	     inputAreas[medialArea].connectToArea(medialAreas[medialArea]); 
	} // end of for (int medialArea = 0; medialArea < numberOfMedialAreas; medialArea++)

	peripheral =
	    new PhaseSegregatorPeripheral(this, inputArea1, circuitArea);
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
	new SimpleNetwork(false); // Single threaded!
	System.exit(0);
    } // end of main ()

    
}// SimpleNetwork
