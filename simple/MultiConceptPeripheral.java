package Simple;
import Base.*;
import Remote.*;
import java.util.*;
//import java.rmi.*;
import Utils.*;

/**
 * Two areas: input and circuit.
 * Two concepts and balancing number of noise concepts in input area.
 *
 * Created: Mon Dec 11 02:52:07 2000
 *
 * @author Cengiz Gunay
 * @version
 */

public class MultiConceptPeripheral extends Base.Peripheral {
    Peripheral.Concept a, b;
    Vector noiseConcepts;
    Object inputArea, circuitArea;

    public MultiConceptPeripheral (Network network, Base.Area inputArea, Base.Area circuitArea) {
	super(network);
	this.inputArea = inputArea;
	this.circuitArea = circuitArea;

	allocateConcepts();
    }

    public MultiConceptPeripheral (Network network, Remote.AreaInt inputArea, 
				   Remote.AreaInt circuitArea) {
	super(network);
	this.inputArea = inputArea;
	this.circuitArea = circuitArea;
	
	allocateConcepts();
    }

    void allocateConcepts() {
	if (inputArea instanceof Base.Area) {
	    a = new Peripheral.Concept((Base.Area)inputArea);
	    b = new Peripheral.Concept((Base.Area)inputArea);
	} else {
	    a = new Peripheral.Concept((Remote.AreaInt)inputArea);
	    b = new Peripheral.Concept((Remote.AreaInt)inputArea);
	} // end of else

	int inputReplication, destReplication, destNumberOfNeuroids;

	if (circuitArea instanceof Base.Area) {
	    inputReplication = ((Base.Area)inputArea).getReplication();
	    destReplication = ((Base.Area)circuitArea).getReplication();
	    destNumberOfNeuroids = ((Base.Area)circuitArea).getNumberOfNeuroids();
	} else {
	    try {
		inputReplication = ((Remote.AreaInt)inputArea).getReplication();
		destReplication = ((Remote.AreaInt)circuitArea).getReplication();
		destNumberOfNeuroids = ((Remote.AreaInt)circuitArea).getNumberOfNeuroids(); 
	    } catch (java.rmi.RemoteException e) {
		System.out.println("Cannot call Remote.Area methods.");
		e.printStackTrace();
		return;
	    }
	} // end of else	
	
	// calculate remaining concepts needed to equalize weights between areas
	int remainingConcepts =
	    Math.max(0, (int)Math.ceil((2*Math.sqrt(destReplication * destNumberOfNeuroids)
					- destReplication) /
				       inputReplication)
		     - 2);
	System.out.println("Allocating " + remainingConcepts +
			   " more Concepts in inputArea to balance load...");

	noiseConcepts = new Vector();
	for (int i = 0; i < remainingConcepts; i++) {
	    if (inputArea instanceof Base.Area) {
		noiseConcepts.add(new Peripheral.Concept((Base.Area)inputArea));
	    } else {
		noiseConcepts.add(new Peripheral.Concept((Remote.AreaInt)inputArea));
	    } // end of else
	    
	} // end of for (int i = 0; i < remainingConcepts; i++)
    }

    public void fireInputs() {
	a.fire();
	b.fire();
    }

    public void testOneInput() {
	a.fire();
    }

    public void fireRandomNoise() {
	Iteration.loop(noiseConcepts.iterator(), new Task() {
		int i = 0;
		public void job(Object o) {
		    // 1/300 chance should make all of them fire once during 300 steps(!)
		    int level = (int)(Math.random()*300);
		    if (level == 1) {
			((Peripheral.Concept) o).fire();
			System.out.println("Firing random concept...");
			
		    } else 
			return;
		}
	    });
    }

    public void fireNoise1() {
	Iteration.loop(noiseConcepts.iterator(), new Task() {
		int i = 0;
		public void job(Object o) {
		    if (i++ < noiseConcepts.size()/2) 
			return;
		    else 		    
			((Peripheral.Concept) o).fire();
		}
	    });
    }

    public void fireNoise2() {
	Iteration.loop(noiseConcepts.iterator(), new Task() {
		int i = 0;
		public void job(Object o) {
		    if (i++ > noiseConcepts.size()/2) {
			return;
		    } // end of if (i++ > noiseConcepts.size()/2)
		    
		    ((Peripheral.Concept) o).fire();
		}
	    });
    }
}// MultiConceptPeripheral
