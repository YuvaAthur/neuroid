package router;
import Base.*;
import Remote.*;
import periphery.*;
import java.util.*;
//import java.rmi.*;
import Utils.*;

/**
 * 3 <code>SensoryArea</code>s and 3 medial <code>Area</code>s.
 * Inputs are <code>SensoryNeuroid</code>s.
 * @see SensoryArea
 * @see SensoryNeuroid
 *
 * Created: Mon Dec 11 02:52:07 2000
 * Modified: $Date$
 *
 * @author Cengiz Gunay
 * @version $Revision$ for this file
 */

public class SmallPeripheral extends Base.Peripheral {
    Base.Area inputArea;
    int numberOfItems;
    SensoryArea sensoryArea;

    public SmallPeripheral (Base.Network network, Base.Area inputArea,
			    int numberOfItems) {
	super(network);
	this.inputArea = inputArea;
	this.numberOfItems = numberOfItems;

	createSensoryInputs();
    }

    /**
     * Creates sensoryAreas that hold sensoryNeuroids.
     * Neuroids can be indirectly reached from the areas.
     */
    void createSensoryInputs() {
	sensoryArea = new SensoryArea(network, "sensory-area");
	String name = "S";
	for (int concept = 0; concept < numberOfItems; concept++) 
	    new SensoryNeuroid(sensoryArea, inputArea, name + "-" + concept);
    }

    public void fireInputs() {
	((Neuroid)sensoryArea.neuroids.elementAt(0)).fire(); 
	((Neuroid)sensoryArea.neuroids.elementAt(1)).fire(); 
    }

    public void fireOne(int index) {
	((Neuroid)sensoryArea.neuroids.elementAt(index)).fire(); 
	System.out.println("\nSensory input: " + index + "\n");
    }

    public void testOneInput() {
	// Fire one input in sensory area 1
	((Neuroid)sensoryArea.neuroids.elementAt(0)).fire(); 
    }

    int first, second;

    /**
     * Converts given index number to two-out-of-nine representation adn fires respective
     * neuroids in sensory area.
     *
     * @param index an <code>int</code> value
     */
    void fireTwoOutofNine(int index) {
	
	if (index > (9*8/2)) throw new Error("Index too large...");

	sub(index, 8);

/*	int first = index / 8;
	int second = index % 8;

	if (second >= first) second++;*/

	System.out.println("\nSensory input: (" + first + "," + second + ")\n");

	((Neuroid)sensoryArea.neuroids.elementAt(first)).fire(); 
	((Neuroid)sensoryArea.neuroids.elementAt(second)).fire(); 
	
    }

    void sub(int index, int digit) {
	if (index >= digit) 
	    sub(index - digit, digit - 1);
	else {
	    first = 8 - digit;
	    second = first + index + 1;
	} // end of else	
    }

}
