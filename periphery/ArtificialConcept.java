package neuroidnet.periphery;

import neuroidnet.ntr.*;
import neuroidnet.Utils.*;

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
    implements Concept, Comparable, DumpsData {

    /**
     * Name of the concept
     * @see #ArtificialConcept(Network,HashSet)
     */
    String name;
    
    /**
     * Set of other concepts that this concept represents, if available.
     */
    Set conceptSet;

    /**
     * Excitatory synapse template to instantiate new synapses.
     */
    final Synapse excitatorySynapse = new Synapse(null, null, area.timeConstantM,
						  area.network.deltaT, false, 0);

    /**
     * Create a simple concept.
     *
     * @param network a <code>Network</code> value
     * @param name a <code>String</code> value
     */
    public ArtificialConcept(Network network, String name) {
	super(network.getConceptArea()); 
	this.name = name;

	conceptSet = new HashSet();
	conceptSet.add(this);

	init2();
    }

    /**
     * Create a <code>concept</code> from a conjunction of <code>concept</code>s.
     * Name it by the concatanation of names of causing concepts. 
     * <p>TODO: Raise exception if conceptSet already exists as a key in conceptArea.
     * @param network a <code>Network</code> value
     * @param conceptSet a <code>Vector</code> value
     */
    public ArtificialConcept (Network network, HashSet conceptSet) {
	super(network.getConceptArea());
	this.conceptSet = conceptSet;

	// Create a name for the concept by concatanating causing concept names
	TaskWithReturn toStringTask =
	    new TaskWithReturn() {
		String name = "[ ";
		    
		// spike lists from different synapses separated by space
		public void job(Object o) {
		    name += ((ArtificialConcept)o).getName() + ", "; 
		}
		    
		public Object getValue() {
		    return name + " ]";
		}
	    };
	
	Iteration.loop(conceptSet.iterator(), toStringTask);
	name = (String) toStringTask.getValue();

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
	watch = true;		// Always true for ArtificialConcept
    }

    /**
     * Attaches the neuroid to the concept population.
     * Aborts and throws an exception if population already contains Area.replication
     * number of Neuroids.
     * TODO: How about bidirectional connections?
     * @see Concept#attach
     * @param neuroid a <code>Neuroid</code> value
     */
    public void attach(Neuroid neuroid) throws ConceptSaturatedException {
	    if (synapses.size() >= neuroid.getArea().getReplication()) {
		System.out.println("Replication limit reached for " + this + "\n" +
				   "Cannot attach " + neuroid + ".");
		throw new
		    ConceptSaturatedException("Replication limit reached for concept " +
					      this);
	    }
	    
	Synapse synapse = new Synapse(neuroid, this, excitatorySynapse); // adds to synapses
	Vector synapses = new Vector(1); 
	synapses.add(synapse);	// Local variable
	neuroid.getArea().addAxon(neuroid, synapses);
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
	    if (s.getSrcNeuroid().equals(neuroid)) {
		removed = s;
		i.remove();
		break;		// Out of for
	    } // end of if (s.srcNeuroid.equals(neuroid))
	} // end of for

	if (removed == null) 
	    throw new RuntimeException("Fatal: cannot find neuroid to detach.");

	Vector synapses = (Vector) neuroid.getArea().getAxons().get(neuroid); // TODO: function?
	synapses.remove(removed); // Remove Synapse associated with neuroid
    }

    //public static int id = 0; OBSOLETE

    /**
     * Describe concept in more detail, including static properties.
     * TODO: make an interface that requires this method
     * TODO: this method looks very similar in all, util'ize it?
     *
     * @return a <code>String</code> value
     */
    public String getStatus() {
	String retval = this + " {\n";
	
	// TODO: make this following class common with the one in Network.toString()
	TaskWithReturn toStringTask =
	    new TaskWithReturn() {
		String retval = new String();
		
		public void job(Object o) {
		    retval += "\t" + ((Synapse)o).getSrcNeuroid() + "\n";
		}

		public Object getValue() {
		    return retval;
		}
	    };
	
	Iteration.loop(synapses.iterator(), toStringTask);
	
	retval += (String)toStringTask.getValue() + "}\n";

	return retval;
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

    public String getName() {
	return name;
    }

}// ArtificialConcept
