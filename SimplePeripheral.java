
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
	b = new Concept(inputArea2);
    }

    public SimplePeripheral (Network network, RemoteAreaInt inputArea1, RemoteAreaInt inputArea2,
			     RemoteAreaInt circuitArea) {
	super(network);
	
	a = new Concept(inputArea1);
	b = new Concept(inputArea2);
    }

    public void fireInputs() {
	a.fire();
	b.fire();
    }

    public void testOneInput() {
	a.fire();
    }

}// SimplePeripheral
