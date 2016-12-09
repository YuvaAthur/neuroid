
package neuroidnet.phasesegregator;

import neuroidnet.ntr.*;
import neuroidnet.remote.*;
import neuroidnet.periphery.*;

import java.util.*;
import java.io.*;

import edu.ull.cgunay.utils.*;

// $Id$
/**
 * Provides sequence of inputs for a three level deep network for
 * demonstrating phase segregation.
 *
 * <p>Created: Thu Oct  3 13:47:54 2002
 * <p>Modified: $Date$
 *
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version $Revision$ for this file.
 */

public class ThreeLevelInputSequence extends Peripheral  {
  public ThreeLevelInputSequence (neuroidnet.ntr.Network network,
				  neuroidnet.ntr.Area[] inputAreas,
				  int numberOfItemsPerArea, double segregation) {
    super(network, inputAreas, numberOfItemsPerArea, segregation);
  }

  /**
   * Overloaded to define event for inputs going three levels deep.
   *
   */
  void initEvents() {
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

    super.initEvents();
  }

    void fireObjectA() {
      fireObjectInArea(1, 0);
      fireObjectInArea(1, 1);
      fireObjectInArea(2, 0);
      fireObjectInArea(3, 0);
    }

    void fireObjectB() {
      fireObjectInArea(1, 1);
      fireObjectInArea(1, 2);
      fireObjectInArea(2, 0);
      fireObjectInArea(3, 1);
    }

    void fireObjectC() {
      fireObjectInArea(1, 0);
      fireObjectInArea(1, 2);
      fireObjectInArea(2, 2);
      fireObjectInArea(3, 2);
    }

    void fireObject(int a1, int a1_1, int a2, int a3) {
	((Neuroid)sensoryArea.neuroids.elementAt(a1)).fire(); 
	((Neuroid)sensoryArea.neuroids.elementAt(a1_1)).fire(); 
	((Neuroid)sensoryArea.neuroids.elementAt(a2 + numberOfItemsPerArea)).fire(); 
	((Neuroid)sensoryArea.neuroids.elementAt(a3 + numberOfItemsPerArea*2)).fire(); 
    }

  
}// ThreeLevelInputSequence
