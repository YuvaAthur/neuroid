package neuroidnet.periphery;

import neuroidnet.ntr.*;
import edu.ull.cgunay.utils.*;

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
    implements Concept, Comparable, DumpsData, Expressive {

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
	name = new StringTask("{ ", " }") {
		// concept names separated by comma
		boolean first = true;
		public void job(Object o) {
		    if (!first)
			super.job(", ");
		    else 
			first = false;
		    super.job(((ArtificialConcept)o).getName()); 
		}
	    }.getString(conceptSet);

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
     * <p>TODO: How about bidirectional connections?
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
	    
	    AxonArbor synapses = new AxonArbor(excitatorySynapse, neuroid, area);
	    try {
		synapses.addNeuroid(this); 		 
	    } catch (ResynapseException e) {
		throw new Error("Fatal: " + e);
	    } // end of try-catch
	    

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
     * Describe concept.
     * @return <description>
     */
    public String toString() {
	/*return "Concept #" + hashCode() + ", u = " +
	    numberFormat.format(potential) + ", name: " + name;*/
	return "Concept: " + name;
    }

    /**
     * Add concept set size to result of <code>toString()</code>.
     *
     * @return a <code>String</code> value
     */
    public String getStatus() {
	return this + "(" + synapses.size()
	    /*+ "/" + area.getReplication()*/ + ")";
    }

    /**
     * Describe concept in more detail, including static properties.
     *
     * @return a <code>String</code> value
     */
    public String getProperties() {
	return
	    this.getStatus() + 
	    new StringTask(" {\n", "}\n") {
		public void job(Object o) {
		    super.job("\t" + ((Synapse)o).getSrcNeuroid().getStatus() + "\n");
		}
	    }.getString(synapses);
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
