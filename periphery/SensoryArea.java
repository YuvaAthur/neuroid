package neuroidnet.periphery;

import neuroidnet.ntr.*;

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

public class SensoryArea extends Area {
    public SensoryArea (Network network, String name) {
	// threshold 0.9? get it from somewhere?
	super(network, name, 0, 1, 0, 0.9, false,
	      network.deltaT * 100, 0.001); // refractoryTimeConstant not applicable, dummy value
	network.addArea(this);	// TODO: add areas to network automatically? what about remoteareas?
    }
    
}// SensoryArea
