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

public class Peripheral extends Base.Peripheral {
    Peripheral.Concept a, b;

    public Peripheral (Network network, Area inputArea1, Area inputArea2, Area circuitArea) {
	super(network);
	
	a = new Peripheral.Concept(inputArea1);
	b = new Peripheral.Concept(inputArea1);
    }

    public Peripheral (Network network, Remote.AreaInt inputArea1, Remote.AreaInt inputArea2,
			     Remote.AreaInt circuitArea) {
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
