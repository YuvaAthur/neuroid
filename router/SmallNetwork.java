package router;
import Base.*;
import periphery.*;
//import Remote.*;
/**
 * Builds a network to test hashing quality for a hypothetical routing task.
 * 32 possible addresses should be associated with equal number of output nodes.
 * There exists one input area and one target area.
 *
 * Created: May 4th
 *
 * @author Cengiz Gunay
 * @version $Revision$ for this file
 */

public class SmallNetwork extends Base.Network {
    router.SmallPeripheral peripheral;

    public SmallNetwork (boolean isConcurrent) {
	super(0.01, isConcurrent);		// sets deltaT
    }

    /**
     * One input area and one target area.
     */
    public void build() {
	int replication = 10;

	double period = Neuroid.defaultPeriod();

	Base.Area inputArea =
	    new Base.Area(this, "I", 200, 20, period, 0.9, false);
	Base.Area circuitArea =
	    new Base.Area(this, "C", 650, 20, period, 1.8, false);

	areas.add(inputArea);
	areas.add(circuitArea);

	inputArea.connectToArea(circuitArea);

	peripheral =
	    new router.SmallPeripheral(this,  inputArea, 9);
    }

    /**
     * Simulation plan fro simple routing table creation:
     * fire all possible couples (2 out of 9) with delays and report destination formation.
     */
    public void simulation() {
	int inputs = 32, inputbits = 9;

	// Step 
	double unitTime = 2.0, untilTime = inputbits * unitTime;
	long startTime = System.currentTimeMillis();

	for (int i = 0; i < untilTime/deltaT; i++) {
	    if (i%(unitTime/deltaT) == 0) 
		peripheral.fireOne((int) (i * deltaT / unitTime));
	    step();		// step deltaT
	}

	untilTime = inputs * unitTime;
	for (int i = 0; i < untilTime/deltaT; i++) {
	    //System.out.println("STEP " + i);

	    if (i%(unitTime/deltaT) == 0)
		peripheral.fireTwoOutofNine((int) (i * deltaT / unitTime));

	    step();		// step deltaT
	}
	System.out.println("Elapsed time: " + (System.currentTimeMillis() - startTime) + " milliseconds.");
    }

    public static void main (String[] args) {
	new router.SmallNetwork(false); // Single threaded!
	System.exit(0);
    } // end of main ()

    
}// SimpleNetwork
