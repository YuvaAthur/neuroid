package PhaseSegregator;
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

public class Peripheral extends Base.Peripheral {
    Base.Area[] inputAreas;
    int numberOfItemsPerArea;
    SensoryArea[] sensoryAreas;

    public Peripheral (Base.Network network, Base.Area[] inputAreas,
		       int numberOfItemsPerArea) {
	super(network);
	this.inputAreas = inputAreas;
	this.numberOfItemsPerArea = numberOfItemsPerArea;

	createSensoryInputs();
    }

    /**
     * Creates sensoryAreas that hold sensoryNeuroids.
     * Neuroids can be indirectly reached from the areas.
     */
    void createSensoryInputs() {
	sensoryAreas = new SensoryArea[inputAreas.length];
	for (int area = 0; area < inputAreas.length; area++) {
	    sensoryAreas[area] = new SensoryArea(network, "sensory-area-" + (area + 1));
	    String name = "S" + (area + 1);
	    for (int concept = 0; concept < numberOfItemsPerArea; concept++) 
		sensoryAreas[area].addNeuroid(new SensoryNeuroid(sensoryAreas[area],
								 inputAreas[area],
								 name + "-" + concept));
	}
    }

    public void fireInputs() {
	// Fire one input in sensory area 1
	((Neuroid)sensoryAreas[0].neuroids.elementAt(0)).fire(); 
    }

    public void testOneInput() {
	//	a.fire();
    }

}// MultiConceptPeripheral
