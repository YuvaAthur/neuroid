package Base;

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
    implements Map {

    Hashtable conceptLookup = new Hashtable();

    /**
     * Creates an Area named ConceptArea with initially no neuroids.
     * 
     * @param network a <code>Network</code> value
     */
    public ConceptArea (Network network) {
	// threshold 0.9? get it from somewhere?
	super(network, "ConceptArea", 0, 1, 0, 0.9, false); 
    }
/*
    int getNewId() {
	return numberOfNeuroids + 1;
    }
*/
    // Map interface functions
    public void clear() { conceptLookup.clear(); }
    public boolean containsKey(Object key) { return conceptLookup.containsKey(key); }
    public boolean containsValue(Object value) { return conceptLookup.containsValue(value); }
    public Set entrySet() { return conceptLookup.entrySet(); }
    public Object get(Object key) { return conceptLookup.get(key); }
    public boolean isEmpty() { return conceptLookup.isEmpty(); }
    public Set keySet() { return conceptLookup.keySet(); }
    public Object put(Object key, Object value) {
	numberOfNeuroids++;
	neuroids.add(value);	// Add neuroid to area vector
	return conceptLookup.put(key, value); // and hashtable
    }
    public void putAll(Map t) { conceptLookup.putAll(t); }
    public Object remove(Object key) { return conceptLookup.remove(key); }
    public int size() { return conceptLookup.size(); }
    public Collection values() { return conceptLookup.values(); } 
}// ConceptArea
