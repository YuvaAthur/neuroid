package neuroidnet.Simple;
import neuroidnet.ntr.*;
import neuroidnet.Remote.*;
import java.util.*;
//import java.rmi.*;
import neuroidnet.Utils.*;

/**
 * Two areas: input and circuit.
 * Two concepts and balancing number of noise concepts in input area.
 *
 * Created: Mon Dec 11 02:52:07 2000
 *
 * @author Cengiz Gunay
 * @version
 */

public class MultiConceptPeripheral extends ntr.Peripheral {
    Peripheral.Concept a, b;
    Vector noiseConcepts;
    Object inputArea, circuitArea;

    public MultiConceptPeripheral (Network network, ntr.Area inputArea, ntr.Area circuitArea) {
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
	if (inputArea instanceof ntr.Area) {
	    a = new Peripheral.Concept((ntr.Area)inputArea);
	    b = new Peripheral.Concept((ntr.Area)inputArea);
	} else {
	    a = new Peripheral.Concept((Remote.AreaInt)inputArea);
	    b = new Peripheral.Concept((Remote.AreaInt)inputArea);
	} // end of else

	int inputReplication, destReplication, destNumberOfNeuroids;

	if (circuitArea instanceof ntr.Area) {
	    inputReplication = ((ntr.Area)inputArea).getReplication();
	    destReplication = ((ntr.Area)circuitArea).getReplication();
	    destNumberOfNeuroids = ((ntr.Area)circuitArea).getNumberOfNeuroids();
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
	    if (inputArea instanceof ntr.Area) {
		noiseConcepts.add(new Peripheral.Concept((ntr.Area)inputArea));
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
