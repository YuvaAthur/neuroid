import java.util.*;
import java.rmi.*;
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

public class PhaseSegregatorPeripheral extends Peripheral {
    Concept[] concepts;
    Area[] inputAreas;
    int numberOfItems;

    public PhaseSegregatorPeripheral (Network network, Area[] inputAreas,
				      int numberOfItemsPerArea) {
	super(network);
	this.inputAreas = inputAreas;
	this.numberOfItems = numberOfItems;

	allocateConcepts();
    }

    void allocateConcepts() {
	concepts = new Concept[numberOfItems];
	for (area = 0; area < inputAreas.size; area++) {
	    for (concept = 0; concept < numberOfItemsPerArea; concept++) {
		concepts[area*inputAreas.size + concept] = new Concept(inputAreas[area]);
	    } // end of for (concept = 0; concept < numberOfItems; concept++)
	} // end of for (area = 0; area < inputAreas.size; area++)
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
			((Concept) o).fire();
			System.out.println("Firing random concept...");
		    } else return;
		}});
    }

}// MultiConceptPeripheral
