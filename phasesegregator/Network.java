package neuroidnet.phasesegregator;
import neuroidnet.ntr.*;
import neuroidnet.periphery.*;
//import remote.*;

// $Id$
/**
 * Builds a network to test theoretical phase segregation properties.
 * <p>TODO: Instead of n input areas, put all input concepts in the same area,
 * similar to that of V1-V3, V5 example.
 *
 * <p>Created: Thu Nov 23 03:09:39 2000
 *
 * @author Cengiz Gunay
 * @version $Revision$ for this file
 */

public class Network extends neuroidnet.ntr.Network {
    Peripheral peripheral; //note that it's a phasesegregator.Peripheral

    public Network (boolean isConcurrent) { // 
	super(0.01, isConcurrent);		// sets deltaT
    }

    /**
     * n input areas and m medial areas. See scheme depicted page EX[6]-0] in notes .
     */
    public void build() {
	//TO DO: Implement this method.
	int
	    numberOfNeuroids = 100,
	    replication = 10;

	double
	    period = Neuroid.defaultPeriod(),
	    delay = 3,
	    timeConstantS = 7,
	    timeConstantM = 10,
	    refractoryTimeConstant = 10,
	    threshold,
	    nuBoost = 6.0;	// nu parameter to increase probability of connection
	
	int numberOfMedialAreas = 3,
	    numberOfItemsPerArea = 3,
	    numberOfItems = numberOfItemsPerArea * numberOfMedialAreas;

	/*Area inputArea = new Area("I", numberOfItems*replication, replication,
	  deltaT, period, 0.9);
	  areas.add(inputArea);*/

	Area[] inputAreas = new Area[numberOfMedialAreas];
	for (int medialArea = 0; medialArea < numberOfMedialAreas; medialArea++) {
	    threshold = 0.9;
	    inputAreas[medialArea] =
		new Area(this, "I"+(medialArea+1), numberOfNeuroids, replication,
			 period, threshold, timeConstantM, refractoryTimeConstant);
	    areas.add(inputAreas[medialArea]);
	} // end of for (int medialArea = 0; medialArea < numberOfMedialAreas; medialArea++)

	Area[] medialAreas = new Area[numberOfMedialAreas];
	for (int medialArea = 0; medialArea < numberOfMedialAreas; medialArea++) {
	    threshold = 2 * 0.6; // lowered because of long timeConstantS
	    medialAreas[medialArea] =
		new Area(this, "M"+(medialArea+1), numberOfNeuroids,
			 replication,	
			 period, threshold, timeConstantM, refractoryTimeConstant);
	    areas.add(medialAreas[medialArea]);
	    
	    // Direct connections from input areas
	    inputAreas[medialArea].connectToArea(medialAreas[medialArea], timeConstantS,
						 (medialArea + 1) * delay, nuBoost); 

	    // Relay connections between medial areas
	    if (medialArea > 0) 
		medialAreas[medialArea - 1].connectToArea(medialAreas[medialArea], timeConstantS,
							  delay, nuBoost); 
	} // end of for

	peripheral =
	    new Peripheral(this, inputAreas, numberOfItemsPerArea);
    }

    /**
     * Simulation plan for phase segregation:
     * fire one from each sensoryarea simultaneously
     * delay for some time
     * fire one other from each sensoryarea simultaneously
     * check for illusory conjunctions 
     *	(dump list of Concepts and firing times, to matlab file)
     * @deprecated See <code>advanceTime</code>
     * @see ntr.Network#advanceTime
     */
/*
    public void simulation() {

	// Step 
	double untilTime = 30.0;
	long startTime = System.currentTimeMillis();
	for (int i = 0; i < untilTime/deltaT; i++) {
	    //System.out.println("STEP " + i);

	    // Fire both inputs initially
	    if (i == 0)
		peripheral.testOneInput();

	    step();		// step deltaT
	}
	System.out.println("Elapsed time: " + (System.currentTimeMillis() - startTime) + " milliseconds.");
    }
*/
    /**
     * Create the network and run for 30 msecs. (Single threaded.)
     *
     * @param args a <code>String[]</code> value
     */
    public static void main (String[] args) {
	neuroidnet.ntr.Network network = new Network(false); // Single threaded!
	network.run();
	network.advanceTime(5.00); // Run net for 30 msecs
	network.finale();
	System.exit(0);
    } // end of main ()

    
}// Network
