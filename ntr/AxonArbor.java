package neuroidnet.ntr;

import neuroidnet.remote.*;
import edu.ull.cgunay.utils.*;

import java.lang.*;
import java.util.*;
//import java.rmi.*;


// $Id$
/**
 * Contains all outgoing synapses of a <code>Neuroid</code>
 * that end up in a particular <code>Area</code> with a similar
 * <code>Synapse</code> template. The main reason for this is for 
 * efficient communications in distributed environments.
 * <p>TODO:
 * <ul>
 * <li>May need to provide a fire() method in here.
 * <li>Extend HAshtable instead keying with Area?
 * </ul>
 * <p>Created: Thu Mar  8 16:43:37 2001
 *
 * @author Cengiz Gunay
 * @version $Revision$
 */

public class AxonArbor extends Vector implements Input {
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
    
    /**
     * Get the value of destSynapseTemplate.
     * @return value of destSynapseTemplate.
     */
    public Synapse getDestSynapseTemplate() {
	return destSynapseTemplate;
    }
    
    /**
     * Set the value of destSynapseTemplate.
     * @param v  Value to assign to destSynapseTemplate.
     */
    public void setDestSynapseTemplate(Synapse  v) {
	this.destSynapseTemplate = v;
    }

    /**
     * Constructor. Calls <code>init()</code>.
     * @param destSynapseTemplate Template to use when creating a synapse.
     * @param srcNeuroid from which Neuroid the AxonArbor is emanating.
     * @param destArea to which it is projecting.
     * @see #init
     * @see #addNeuroid
     * @see #addRandomSynapse
     */
    public AxonArbor (Synapse destSynapseTemplate, Neuroid srcNeuroid, Area destArea) {
	super();

	init(destSynapseTemplate, srcNeuroid, destArea);
    }

    /**
     * Specifies initial capacity for the <code>Vector</code> holding the synapses.
     * Calls <code>init()</code>.
     * @see Vector
     * @param destSynapseTemplate a <code>Synapse</code> value
     * @param initialCapacity an <code>int</code> value
     * @param destArea to which it is projecting.
     * @see #init
     * @see #addNeuroid
     * @see #addRandomSynapse
     */
    public AxonArbor (Synapse destSynapseTemplate, Neuroid srcNeuroid, Area destArea,
		      int initialCapacity) {
	super(initialCapacity);

	init(destSynapseTemplate, srcNeuroid, destArea);
    }

    /**
     * Called from all constructors. Cannot be overridden or override another.
     * Attaches the <code>AxonArbor</code> to the <code>srcNeuroid</code>.
     * 
     * @param destSynapseTemplate a <code>Synapse</code> value
     * @param srcNeuroid a <code>Neuroid</code> value
     * @param destArea an <code>Area</code> value
     */
    private final void init(Synapse destSynapseTemplate, Neuroid srcNeuroid, Area destArea) {
	this.destSynapseTemplate = destSynapseTemplate;
	this.srcNeuroid = srcNeuroid;
	this.destArea = destArea;

	// Add this to the outgoing hash of the Area of srcNeuroid
	srcNeuroid.getArea().addAxon(srcNeuroid, this); 
    }

    /**
     * Adds a synapse to list of synapses
     * only if it doesn't already exist.
     *
     * @param synapse a <code>Synapse</code> value
     * @exception ResynapseException if the <code>synapse</code> is already in arbor.
     */
    public void addSynapse(final Synapse synapse) throws ResynapseException {

	// Check all existing synapses for the same postsynaptic neuron
	try {
	    Iteration.loop(iterator(), new Task() { 
		    public void job(Object o) throws TaskException {
			if (((Synapse) o).getDestNeuroid().equals(synapse.getDestNeuroid())) 
			    throw new BreakOutOfIterationException(o);
		    }});	     
	} catch (BreakOutOfIterationException e) {
	    throw new ResynapseException("Synapsing on same " +
					 ((Synapse)e.getValue()).getDestNeuroid() +
					 ", try again!");	    
	} // end of try-catch

	// otherwise accept new synapse
	add(synapse);
    }

    /**
     * Creates a new synapse connected to a random member of the <code>Area</code>.
     * @see Area#getRandomNeuroid
     */
    public void addRandomSynapse() throws ResynapseException {
	addNeuroid(destArea.getRandomNeuroid());
    }

    /*
     * Add a synapse with predefined characteristics.
     * <p> TODO: throw a more meaningful exception
     * @see #addNeuroid
     * @param destNeuroid a <code>Neuroid</code> value
     * @exception ResynapseException (from <code>addNeuroid</code>)
     * @exception RuntimeException if <code>destNeuroid</code> is not part of
     * <code>destArea</code>.
     * @deprecated Replaced by addNeuroid     
    public void createSynapse(Neuroid destNeuroid) throws ResynapseException {
    }
    */

    /**
     * Adds a postsynaptic neuroid to the <code>AxonArbor</code>. Uses the 
     * template synapse of this object to create a <code>Synapse</code>
     * with predefined characteristics.
     * @see #addSynapse
     * @see #addNeuroid
     * @see #destSynapseTemplate
     * @param destNeuroid the <code>Neuroid</code> to add
     * @exception ResynapseException (from <code>addSynapse</code>)
     * @exception RuntimeException if <code>destNeuroid</code> is not part of
     * <code>destArea</code>.
     */
    public void addNeuroid(Neuroid destNeuroid) throws ResynapseException, RuntimeException {
	if (!destArea.neuroids.contains(destNeuroid))
	    throw new RuntimeException("Error: In AxonArbor, destNeuroid is not" +
				       "contained in destArea.");
	addSynapse(new Synapse(srcNeuroid, destNeuroid, destSynapseTemplate));
	//	createSynapse(destNeuroid);
    }

    // implementation of neuroidnet.ntr.Input interface

    /**
     * Spikes are received at contained synapses.
     */
    public void fire() {
	UninterruptedIteration.loop(iterator(), new Task() {
		public void job(Object o) {
		    try {
			((SynapseInt)o).receiveSpike();
		    } catch (java.rmi.RemoteException e) {
			System.out.println("Cannot call SynapseInt");
		    }
		}});
    }

    
}// AxonArbor
