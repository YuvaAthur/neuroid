package neuroidnet.periphery;

import neuroidnet.ntr.*;
import neuroidnet.utils.*;
import java.util.*;

// $Id$
/**
 * Artificial area for holding <code>Concept</code>s. Contains a hashtable for associating
 * conjunction of concepts to new concepts.
 *
 * Created: Tue Mar 27 00:47:50 2001
 * Modified: $Date$
 *
 * @author Cengiz Gunay
 * @version $Revision$ for this file
 */

public class ConceptArea extends Area
    implements Map, DumpsData {

    /**
     * Hashtable pointing from vector (set?) of concepts to concepts contained in here.
     */
    Hashtable conceptLookup = new Hashtable();

    /**
     * Creates an Area named ConceptArea with initially no neuroids.
     * 
     * @param network a <code>Network</code> value
     */
    public ConceptArea (Network network) {
	// threshold 0.9? get it from somewhere? SRM params defined here!!!
	// timeConstantM, refractoryTimeConstant << 1
	super(network, "ConceptArea", 0, 1, 0, 0.9, false, 0.001, 0.001); 
    }
    /*
      int getNewId() {
      return numberOfNeuroids + 1;
      }
    */

    /**
     * Report on concepts in this area one by one.
     *
     * @return the description to be printed out as a <code>String</code> value
     */
    public String getStatus() {
	String retval = super.getStatus() + "\n";
	
	TaskWithReturn neuroidsToStringTask = new StringTask() {
		public void job(Object o) {
		    this.retval += "" + ((ArtificialConcept)o).getStatus() + "\n";
		}
	    };
	
	Iteration.loop(neuroids.iterator(), neuroidsToStringTask);
	
	retval += (String)neuroidsToStringTask.getValue();

	return retval;
    }

    /**
     * Dump synaptic activity of concepts contained to output (matlab file?). Called by:
     * <p>TO DO: 
     * @see Network#finale
     */
    public String dumpData() {
	String retval =
	    "%% Matlab script created by the Neuroidal network\n\n" +
	    "lastTime = " + time + ";\n" + 
	    "numberOfConcepts = " + neuroids.size() + ";" +
	    "figure;\n\n";
	
	TaskWithReturn toStringTask = new StringTask() {
		int id = 0;
		
		public void job(Object o) {
		    String conceptId = "concept" + id;
		    this.retval +=
			"%% " + o + "\n" + // Remark
			conceptId + "name = '" + o + "';\n" +
			conceptId + " = [" + ((ArtificialConcept)o).dumpData() + "];\n" +
			"subplot(numberOfConcepts, 1, " + (id + 1) + ");\n" +
			"stem(" + conceptId + ", ones(size(" + conceptId + ", 2)), 'filled');\n" +
			"axis([0 lastTime 0 1]);\n" +
			"text(0, 0.5, '" + o + "');\n\n";
		    id++;
		}
	    };
	
	Iteration.loop(neuroids.iterator(), toStringTask);
	
	retval += (String)toStringTask.getValue();

	return retval;
	
    }

    /**
     * Convert Vector to (Tree)Set for avoiding mistake of vector
     * with same content but different order pointing to different buckets!
     * <p>TODO: Once sender ensures sending TreeSet or HashSet, this translation is redundant!
     * @see #get
     * see #put
     * @param key an <code>Object</code> value
     * @return a <code>TreeSet</code> value
     */
    Set convertKey(Object key) {
	Set set;
	if (key instanceof Concept) {
	    set = new TreeSet();
	    set.add(key);
	} else 
	    set = new TreeSet((Collection)key);

	return set;
    }

    // Map interface functions delegated to conceptLookup (with additional checks)
    public void clear() { conceptLookup.clear(); }
    public boolean containsKey(Object key) { return conceptLookup.containsKey(convertKey(key)); }
    public boolean containsValue(Object value) { return conceptLookup.containsValue(value); }
    public Set entrySet() { return conceptLookup.entrySet(); }
    public Object get(Object key) {
	Object convertedKey = convertKey(key);
	System.out.println("Searching " + convertedKey + " in conceptArea.");
	return conceptLookup.get(convertedKey);
    }
    public boolean isEmpty() { return conceptLookup.isEmpty(); }
    public Set keySet() { return conceptLookup.keySet(); }
    public Object put(Object key, Object value) {
	// Raise error if neuroid is not part of conceptarea.
	if (!neuroids.contains(value)) 
	    throw new RuntimeException("Concept " + value + " not in Area");
	numberOfNeuroids++;	// Obsolete?
	Object convertedKey = convertKey(key);
	System.out.println("Associating " + convertedKey + " to " + value + " in conceptarea.");

	return conceptLookup.put(convertedKey, value); // and hashtable
    }
    public void putAll(Map t) { conceptLookup.putAll(t); }
    public Object remove(Object key) { return conceptLookup.remove(convertKey(key)); }
    public int size() { return conceptLookup.size(); }
    public Collection values() { return conceptLookup.values(); } 
}// ConceptArea
