package PhaseSegregator;
import Base.*;
import Remote.*;
import periphery.*;
import java.util.*;
//import java.rmi.*;
import Utils.*;

/**
 * Creates inputs in one <code>SensoryArea</code> that projects to
 * neuroids in 3 input <code>Area</code>s. Inputs are represented by <code>SensoryNeuroid</code>s.
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
    SensoryArea sensoryArea;
    
    public Peripheral (Base.Network network, Base.Area[] inputAreas,
		       int numberOfItemsPerArea) {
	super(network);
	this.inputAreas = inputAreas;
	this.numberOfItemsPerArea = numberOfItemsPerArea;

	createSensoryInputs();
    }

    /**
     * Calls <code>testOneInput()</code> at time 0.
     * @see #testOneInput
     */
    protected void eventsAtThisTime() {
	// Fire both inputs initially
	if (time == 0.00)
	    testOneInput();
    }

    /**
     * Creates sensoryAreas that hold sensoryNeuroids.
     * Neuroids can be indirectly reached from the areas.
     */
    void createSensoryInputs() {
	//sensoryAreas = new SensoryArea[inputAreas.length];
	sensoryArea/*s[areaNo]*/ = new SensoryArea(network, "sensory-area"/* + (areaNo + 1)*/);
	for (int areaNo = 0; areaNo < inputAreas.length; areaNo++) {
	    String name = "S" + (areaNo + 1);
	    for (int concept = 0; concept < numberOfItemsPerArea; concept++) 
		new SensoryNeuroid(sensoryArea/*s[areaNo]*/, inputAreas[areaNo],
				   name + "-" + concept);
	}
    }

    public void fireInputs() {
    }

    public void testOneInput() {
	// Fire one input in sensory area 1
	((Neuroid)sensoryArea/*s[0]*/.neuroids.elementAt(0)).fire(); 
	((Neuroid)sensoryArea/*s[0]*/.neuroids.elementAt(1)).fire(); 
	((Neuroid)sensoryArea/*s[1]*/.neuroids.elementAt(0 + numberOfItemsPerArea)).fire(); 
	((Neuroid)sensoryArea/*s[2]*/.neuroids.elementAt(0 + numberOfItemsPerArea*2)).fire(); 
    }

}
