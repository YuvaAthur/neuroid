package PhaseSegregator;
import Base.*;
import periphery.*;
//import Remote.*;
/**
 * Builds a network to test theoretical phase segregation properties.
 * <p>TODO: Instead of n input areas, put all input concepts in the same area,
 * similar to that of V1-V3, V5 example.
 *
 * Created: Thu Nov 23 03:09:39 2000
 *
 * @author Cengiz Gunay
 * @version $Revision$ for this file
 */

public class  Network extends Base.Network {
    PhaseSegregator.Peripheral peripheral; //

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
	    threshold;
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
		new Area(this, "I"+(medialArea+1), numberOfNeuroids,
			 replication, period, threshold, timeConstantM);
	    areas.add(inputAreas[medialArea]);
	} // end of for (int medialArea = 0; medialArea < numberOfMedialAreas; medialArea++)

	Area[] medialAreas = new Area[numberOfMedialAreas];
	for (int medialArea = 0; medialArea < numberOfMedialAreas; medialArea++) {
	    threshold = 2 * 0.35; // lowered because of long timeConstantS
	    medialAreas[medialArea] =
		new Area(this, "M"+(medialArea+1), numberOfNeuroids,
			 replication, period, threshold, timeConstantM);
	    areas.add(medialAreas[medialArea]);
	    
	    // Direct connections from input areas
	    inputAreas[medialArea].connectToArea(medialAreas[medialArea], timeConstantS,
						 (medialArea + 1) * delay); 

	    // Relay connections between medial areas
	    if (medialArea > 0) 
		medialAreas[medialArea - 1].connectToArea(medialAreas[medialArea], timeConstantS,
							  delay); 
	} // end of for

	peripheral =
	    new PhaseSegregator.Peripheral(this, inputAreas, numberOfItemsPerArea);
    }

    /**
     * Simulation plan for phase segregation:
     * fire one from each sensoryarea simultaneously
     * delay for some time
     * fire one other from each sensoryarea simultaneously
     * check for illusory conjunctions 
     *	(dump list of Concepts and firing times, to matlab file)
     * 
     */
    public void simulation() {

	// Step 
	double untilTime = 20.0;
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

    public static void main (String[] args) {
	new PhaseSegregator.Network(false); // Single threaded!
	System.exit(0);
    } // end of main ()

    
}// SimpleNetwork
