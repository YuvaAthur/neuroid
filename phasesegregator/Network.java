package neuroidnet.phasesegregator;

import neuroidnet.ntr.*;
import edu.ull.cgunay.utils.plots.*;
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
	super(0.1, isConcurrent);		// sets deltaT
    }

    /**
     * n input areas and m medial areas. See scheme depicted page EX[6]-0] in notes .
     */
    public void build() {
	int
	    numberOfNeuroids = 100,
	    replication = 10;

	double
	    period = SRMNeuroid.defaultPeriod(),
	    delay = 3,
	    timeConstantS = 7,
	    timeConstantM = 21,	// 3*timeConstantS
	    refractoryTimeConstant = 10,
	    segregation = timeConstantS + 2*timeConstantM + delay,
	    threshold,
	    nuBoost = 6.0;	// nu parameter to increase probability of connection

	Class neuroidType = PeakerNeuroid.class;
	
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
			 period, threshold, timeConstantM, refractoryTimeConstant,
			 neuroidType);
	    areas.add(inputAreas[medialArea]);
	} // end of for (int medialArea = 0; medialArea < numberOfMedialAreas; medialArea++)

	Area[] medialAreas = new Area[numberOfMedialAreas];
	for (int medialArea = 0; medialArea < numberOfMedialAreas; medialArea++) {
	    threshold = 2 * 0.6; // lowered because of long timeConstantS
	    medialAreas[medialArea] =
		new Area(this, "M"+(medialArea+1), numberOfNeuroids,
			 replication, period, threshold, timeConstantM, refractoryTimeConstant,
			 neuroidType);
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
	    new Peripheral(this, inputAreas, numberOfItemsPerArea, segregation);
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

	try {
	    // TODO: Add 10 neuroids from M1
	    Neuroid n = network.getNeuroid("M1", 1);
	    network.addWatch(n);

	    network.advanceTime(15.00); // Run net 

	    NeuroidProfile profile = n.getProfile();
	    Plot plot = new MembranePotentialPlot("trial", null, profile);
	    Grapher grapher = new GNUPlot();
	    System.out.println("Grapher response: ");
	    grapher.display(plot, System.out);

	    network.finale();
	    System.exit(0);
	     
	} catch (NameNotFoundException e) {
	    throw new Error("Fatal, cannot get neuroid reference");
	} // end of try-catch
	catch (GrapherNotAvailableException e) {
	    throw new Error("Unable to initialize gnuplot..");
	} // end of catch
	

    } // end of main ()

    
}// Network
