package neuroidnet.simple;
import neuroidnet.ntr.*;
import neuroidnet.remote.*;

/**
 * OBSOLETE: first trial, is not up-to-date
 * SimplePeripheral.java
 * Simple peripheral for demonstration purposes.
 *
 * Created: Wed Nov 29 01:59:09 2000
 *
 * @author Cengiz Gunay
 * @version
 * @deprecated 
 */

public class Peripheral extends ntr.Peripheral {
    Peripheral.Concept a, b;

    public Peripheral (Network network, Area inputArea1, Area inputArea2, Area circuitArea) {
	super(network);
	
	a = new Peripheral.Concept(inputArea1);
	b = new Peripheral.Concept(inputArea1);
    }

    public Peripheral (Network network, AreaInt inputArea1, AreaInt inputArea2,
		       AreaInt circuitArea) {
	super(network);
	
	a = new Peripheral.Concept(inputArea1);
	b = new Peripheral.Concept(inputArea1);
    }

    public void fireInputs() {
	a.fire();
	b.fire();
    }

    public void testOneInput() {
	a.fire();
    }

}// SimplePeripheral
