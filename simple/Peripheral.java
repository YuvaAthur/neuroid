package Simple;
import Base.*;
import Remote.*;

/**
 * SimplePeripheral.java
 * Simple peripheral for demonstration purposes.
 *
 * Created: Wed Nov 29 01:59:09 2000
 *
 * @author Cengiz Gunay
 * @version
 */

public class SimplePeripheral extends Peripheral {
    Concept a, b;

    public SimplePeripheral (Network network, Area inputArea1, Area inputArea2, Area circuitArea) {
	super(network);
	
	a = new Concept(inputArea1);
	b = new Concept(inputArea1);
    }

    public SimplePeripheral (Network network, Remote.AreaInt inputArea1, Remote.AreaInt inputArea2,
			     Remote.AreaInt circuitArea) {
	super(network);
	
	a = new Concept(inputArea1);
	b = new Concept(inputArea1);
    }

    public void fireInputs() {
	a.fire();
	b.fire();
    }

    public void testOneInput() {
	a.fire();
    }

}// SimplePeripheral
