package Base;
import Base.*;
import Remote.*;
import java.lang.*;
import java.util.*;
//import java.rmi.*;
import Utils.*;

// $Id$
/**
 * Contains all synapses of a <code>Neuroid</code> that end up in a particular <code>Area</code>.
 * 
 * <p>Created: Thu Mar  8 16:43:37 2001
 *
 * @author Cengiz Gunay
 * @version $Revision$
 */

public class AxonArbor extends Vector {
    /**
     * Axon's owner neuroid.
     * @see Neuroid
     */
    Neuroid neuroid;

    public AxonArbor () {
	super();
    }

    public AxonArbor (int initialCapacity) {
	super(initialCapacity);
    }
    
    /**
     * Adds a synapse to list of synapses
     * only if it doesn't already exist.
     *
     * @param synapse a <code>Synapse</code> value
     * @exception ResynapseException if the <code>synapse</code> is already in arbor.
     */
    public void addSynapse(Synapse synapse) throws ResynapseException {

	// Check all existing synapses for the same postsynaptic neuron
	Object[] p = { synapse, new Boolean(false) };
	Iteration.loop(iterator(), new Utils.TaskWithParam() { 
		public void job(Object o, Object[] p) {
		    Synapse existingSynapse = (Synapse) o;
		    Synapse newSynapse = (Synapse) p[0];

		    if (existingSynapse.getDestNeuroid().equals(newSynapse.getDestNeuroid())) {
			p[1] = new Boolean(true);
		    } // end of if

		}}, p);

	
	/*if (((Boolean)p[1]).booleanValue()) 
	    // throw exception if neuroid found in existing synapses
	    throw new ResynapseException("Synapsing on same neuron, try again!");
	else */
	    // otherwise accept new synapse
	    add(synapse);
    }
    
}// AxonArbor
