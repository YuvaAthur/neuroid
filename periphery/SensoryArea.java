package periphery;

// $Id$
/**
 * Area that holds inputs, ie. <code>SensoryNeuroid</code>s. This area should not be in
 * the list of areas that the network contains.
 * @see SensoryNeuroid
 * @see SensoryConcept
 *
 * <p>Created: Mon Apr  2 00:32:27 2001
 * <p>Modified: $Date$
 *
 * @author Cengiz Gunay
 * @version $Revision$ for this file.
 */

public class SensoryArea extends Base.Area {
    public SensoryArea (Base.Network network, String name) {
	// threshold 0.9? get it from somewhere?
	super(network, name, 0, 1, 0, 0.9, false, 0.001); 
	network.addArea(this);	// TODO: add areas to network automatically? what about remoteareas?
    }
    
}// SensoryArea
