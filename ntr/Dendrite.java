
package neuroidnet.ntr;

import java.util.*;

// $Id$
/**
 * <code>Dendrite</code> is a <code>Vector</code> that holds incoming
 * <code>Synapse</code>s belonging to a neuroid. Its functionality is
 * similar to that of <code>AxonArbor</code>, although differing in
 * organizational location of synapses. The latter is a list of synapses
 * associated with an axon, whereas this one is the synapses on a dendrite.
 *
 *
 * <p>Created: Sun Nov  4 00:26:26 2001
 * <p>Modified: $Date$
 *
 * @see ntr.AxonArbor
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version $Revision$ for this file.
 */

public class Dendrite extends Vector  {
    public Dendrite () {
	
    }
    
    
    /**
     * Adds a synapse to the vector.
     * @see ntr.AxonArbor#addSynapse
     * @param synapse 
     */
    public synchronized boolean add(Synapse synapse) {
	return super.add(synapse);
    }

}// SynapseVector
