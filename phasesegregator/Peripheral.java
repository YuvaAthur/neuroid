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
    
  public Peripheral (neuroidnet.ntr.Network network,
		     neuroidnet.ntr.Area[] inputAreas,
		     int numberOfItemsPerArea, double segregation) {
    super(network);
    this.inputAreas = inputAreas;
    this.numberOfItemsPerArea = numberOfItemsPerArea;
    this.segregation = segregation;

    createSensoryInputs();

    initEvents();
  }

  /**
   * Sets the <code>eventIterator</code> to first position.
   * @see #eventIterator
   * @see #getNext
   */
  void initEvents() {
    eventIterator = events.keySet().iterator();
    getNext();
  }

  /**
   * Iterates to next event. Events are ordered according to time of
   * occurrance.
   * @see #eventIterator
   * @see #nextTime
   */
  final void getNext() {
    try {
      nextTime = (Double) eventIterator.next();	     
    } catch (NoSuchElementException e) {
      nextTime = new Double(Double.POSITIVE_INFINITY);
    } // end of try-catch
  }

  /**
   * If <code>nextTime</code> is reached, call <code>job()</code>.
   * For all registered <code>events</code>, pop and check for if
   * its time has come. 
   * @see #events
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
  final void createSensoryInputs() {
    //sensoryAreas = new SensoryArea[inputAreas.length];
    sensoryArea = new SensoryArea(network, "sensory-area"/* + (areaNo + 1)*/);
    for (int areaNo = 0; areaNo < inputAreas.length; areaNo++) {
      String name = "S" + (areaNo + 1);
      for (int concept = 0; concept < numberOfItemsPerArea; concept++) 
	new SensoryNeuroid(sensoryArea, inputAreas[areaNo],
			   name + "-" + concept);
    }
  }


  /**
   * Fire one input in sensory area.
   */
  void testOneInput() {
    ((Neuroid)sensoryArea.neuroids.elementAt(0)).fire(); 
  }

    void fireObjectInArea(int inputArea, int offset) {
	((Neuroid)sensoryArea.neuroids.elementAt(offset + numberOfItemsPerArea*(inputArea - 1))).fire(); 
    }


    /**
     * For serialization support.
     *
     * @param in a <code>java.io.ObjectInputStream</code> value
     * @exception IOException if an error occurs
     * @exception ClassNotFoundException if an error occurs
     */
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
