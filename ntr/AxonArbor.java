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
    Neuroid srcNeuroid;

    /**
     * Describe variable <code>destNeuroid</code> here.
     */
    Area destArea;
 
    /**
     * Template to create all synapses of this AxonArbor.
     */
    Synapse destSynapseTemplate;

    public AxonArbor (Synapse destSynapseTemplate, Neuroid srcNeuroid, Area destArea) {
	super();

	init(destSynapseTemplate, srcNeuroid, destArea);
    }

    /**
     * Specifies initial capacity for the Vector holding the synapses.
     * @see Vector
     * @param destSynapseTemplate a <code>Synapse</code> value
     * @param initialCapacity an <code>int</code> value
     */
    public AxonArbor (Synapse destSynapseTemplate, Neuroid srcNeuroid, Area destArea,
		      int initialCapacity) {
	super(initialCapacity);

	init(destSynapseTemplate, srcNeuroid, destArea);
    }

    /**
     * Common to constructors.
     * Cannot be overridden or override another.
     * 
     * @param destSynapseTemplate a <code>Synapse</code> value
     */
    private void init(Synapse destSynapseTemplate, Neuroid srcNeuroid, Area destArea) {
	this.destSynapseTemplate = destSynapseTemplate;
	this.srcNeuroid = srcNeuroid;
	this.destArea = destArea;
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
	
	if (((Boolean)p[1]).booleanValue()) 
	    // throw exception if neuroid found in existing synapses
	    throw new ResynapseException("Synapsing on same neuron, try again!");
	else 
	    // otherwise accept new synapse
	    add(synapse);
    }

    /**
     * Creates a new synapse connected to a random member of the <code>Area</code>.
     * @see deltaT
     * @return a <code>Synapse</code> value */
    public Synapse createRandomSynapse() {
	return createSynapse(destArea.getRandomNeuroid());
    }

    /**
     * Create a synapse with predefined characteristics.
     * @param neuroid a <code>Neuroid</code> value
     * @return a <code>Synapse</code> value
     */
    public Synapse createSynapse(Neuroid destNeuroid) {
	return new Synapse(srcNeuroid, destNeuroid, destSynapseTemplate);
    }

    
}// AxonArbor
