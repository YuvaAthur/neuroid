package Base;
import java.lang.*;
import java.util.*;

// $Id$
/**
 * Implementation of a <code>Concept</code> that listens to a population of UM neuroids
 * which represent a certain concept.
 * @see Neuroid
 *
 * Created: Mon Mar 19 23:00:24 2001
 * Modified: $Date$
 * @author Cengiz Gunay
 * @version $Revision$ for this file
 */

public class ArtificialConcept extends Neuroid
    implements Concept, Comparable {

    /**
     * Name of the concept
     */
    String name;
    
    /**
     * Set of other concepts that this concept represents, if available.
     */
    Set conceptSet;

    /**
     * Excitatory synapse template to instantiate new synapses.
     */
    final Synapse excitatorySynapse = new Synapse(null, null, 1, area.network.deltaT, false);

    /**
     * Create a simple concept.
     *
     * @param network a <code>Network</code> value
     * @param name a <code>String</code> value
     */
    public ArtificialConcept(Network network, String name) {
	super(network.conceptArea); 
	this.name = name;

	conceptSet = new HashSet();
	conceptSet.add(this);

	init2();
    }

    /**
     * Create a <code>concept</code> from a conjunction of <code>concept</code>s.
     * <p>TODO: Raise exception if conceptSet already exists as a key in conceptArea.
     * @param network a <code>Network</code> value
     * @param conceptSet a <code>Vector</code> value
     */
    public ArtificialConcept (Network network, HashSet conceptSet) {
	super(network.conceptArea);
	this.conceptSet = conceptSet;

	name = conceptSet.toString(); // TODO: ???

	init2();
    }

    /**
     * Called by all contructors. Adds entry in hashtable of <code>conceptArea</code>.
     * @see ConceptArea
     * @param network a <code>Network</code> value
     * @param conceptSet a <code>Vector</code> value
     * @param name a <code>String</code> value
     */
    void init2() {
	if (conceptSet != null) 
	    ((ConceptArea)area).put(conceptSet, this); 
	else // Add redundant entry in hashtable if conceptSet is empty
	    ((ConceptArea)area).put(this, this); 
    }

    /**
     * Attaches the neuroid to the concept population.
     * TODO: How about bidirectional connections?
     * @see Concept#attach
     * @param neuroid a <code>Neuroid</code> value
     */
    public void attach(Neuroid neuroid) {
	Synapse synapse = new Synapse(neuroid, this, excitatorySynapse);
	Vector synapses = new Vector(1); 
	synapses.add(synapse);
	neuroid.area.addAxon(neuroid, synapses);
    }

    /**
     * Detaches the neuroid from the concept population.
     * @see Concept#detach
     * @param neuroid a <code>Neuroid</code> value
     */
    public void detach(Neuroid neuroid) {
	Synapse removed = null;
	// Search in synapses and delete the synapse (both from here and from the area's axons)
	for (Iterator i = synapses.iterator(); i.hasNext() ;) {
	    Synapse s = (Synapse) i.next();
	    if (s.srcNeuroid.equals(neuroid)) {
		removed = s;
		i.remove();
		break;		// Out of for
	    } // end of if (s.srcNeuroid.equals(neuroid))
	} // end of for

	if (removed == null) 
	    throw new RuntimeException("Fatal: cannot find neuroid to detach.");

	Vector synapses = (Vector) neuroid.area.axons.get(neuroid); // TODO: function?
	synapses.remove(removed); // Remove Synapse associated with neuroid
    }


    /**
     * Dump synaptic activity to output (file?). TO DO: do it!
     *
     */
    public void dumpData() {
    }

    
    /**
     * Describe concept.
     * <p>TODO: remove unneccessary info?
     * @return <description>
     */
    public String toString() {
	/*return "Concept #" + hashCode() + ", u = " +
	    numberFormat.format(potential) + ", name: " + name;*/
	return "Concept: " + name + "(" + synapses.size()
	    /*+ "/" + area.getReplication()*/ + ")";
    }

    /**
     * Required for implementing the Comparable interface for use in TreeMap implementation
     * in ConceptArea.
     * @see ConceptArea 
     * @param o an <code>Object</code> value
     * @return an <code>int</code> value
     */
    public int compareTo(Object o) {
	if (!(o instanceof Concept)) 
	    throw new ClassCastException("" + o);

	Integer
	    me = new Integer(hashCode()),
	    you = new Integer(o.hashCode()); 
	
	return me.compareTo(you);
    }

    public Set getConceptSet() {
	return conceptSet;
    }

}// ArtificialConcept
