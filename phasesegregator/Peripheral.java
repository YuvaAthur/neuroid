package neuroidnet.phasesegregator;
import neuroidnet.ntr.*;
import neuroidnet.remote.*;
import neuroidnet.periphery.*;

import java.util.*;
import java.io.*;

import edu.ull.cgunay.utils.*;

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

public class Peripheral extends neuroidnet.ntr.Peripheral {
    neuroidnet.ntr.Area[] inputAreas;
    int numberOfItemsPerArea;
    SensoryArea sensoryArea;
    double segregation;
    Map events = new TreeMap();
    transient Iterator eventIterator;
    Double nextTime;
    
    public Peripheral (neuroidnet.ntr.Network network, neuroidnet.ntr.Area[] inputAreas,
		       int numberOfItemsPerArea, double segregation) {
	super(network);
	this.inputAreas = inputAreas;
	this.numberOfItemsPerArea = numberOfItemsPerArea;
	this.segregation = segregation;

	createSensoryInputs();

	events.put(new Double(0.00), new Task() {
		public void job(Object o) {
		    fireObjectA();
		}});

	events.put(new Double(segregation), new Task() {
		public void job(Object o) {
		    fireObjectB();
		}});

	events.put(new Double(2 * segregation), new Task() {
		public void job(Object o) {
		    fireObjectC();
		}});

	eventIterator = events.keySet().iterator();
	getNext();
    }

    void getNext() {
	try {
	    nextTime = (Double) eventIterator.next();	     
	} catch (NoSuchElementException e) {
	    nextTime = new Double(Double.POSITIVE_INFINITY);
	} // end of try-catch
    }

    /**
     * Calls <code>testOneInput()</code> at time 0.
     * @see #testOneInput
     */
    protected void eventsAtThisTime() {

	if (time > nextTime.doubleValue()) {
	    try {
		((Task)events.get(nextTime)).job(null);
		getNext();
	    } catch (TaskException e) {
		throw new Error("Fatal: " + e);
	    } // end of try-catch
	} // end of if (time > nextTime)
	/*
	// Fire both inputs initially
	if (time == 0.00)
	    fireObjectA();
	else if (time == segregation)
	    fireObjectB();
	else if (time == 2*segregation)
	    fireObjectC();
*/
    }

    /**
     * Creates sensoryAreas that hold sensoryNeuroids.
     * Neuroids can be indirectly reached from the areas.
     */
    void createSensoryInputs() {
	//sensoryAreas = new SensoryArea[inputAreas.length];
	sensoryArea = new SensoryArea(network, "sensory-area"/* + (areaNo + 1)*/);
	for (int areaNo = 0; areaNo < inputAreas.length; areaNo++) {
	    String name = "S" + (areaNo + 1);
	    for (int concept = 0; concept < numberOfItemsPerArea; concept++) 
		new SensoryNeuroid(sensoryArea, inputAreas[areaNo],
				   name + "-" + concept);
	}
    }

    void fireObjectA() {
	fireObject(0, 1, 0, 0);
    }

    void fireObjectB() {
	fireObject(1, 2, 0, 1);
    }

    void fireObjectC() {
	fireObject(0, 2, 2, 2);
    }

    void fireObject(int a1, int a1_1, int a2, int a3) {
	((Neuroid)sensoryArea.neuroids.elementAt(a1)).fire(); 
	((Neuroid)sensoryArea.neuroids.elementAt(a1_1)).fire(); 
	((Neuroid)sensoryArea.neuroids.elementAt(a2 + numberOfItemsPerArea)).fire(); 
	((Neuroid)sensoryArea.neuroids.elementAt(a3 + numberOfItemsPerArea*2)).fire(); 
    }

    void testOneInput() {
	// Fire one input in sensory area
	((Neuroid)sensoryArea.neuroids.elementAt(0)).fire(); 
    }

    private void readObject(java.io.ObjectInputStream in)
	throws IOException, ClassNotFoundException {
	in.defaultReadObject(); // Real method that does reading
	
	// Transient variables
	eventIterator = events.keySet().iterator();

	Double fromStart;
	do {
	    fromStart = (Double) eventIterator.next();
	} while (!nextTime.equals(fromStart)); // end of while (nextTime.equals())
	
    }

}
