package Base;

import java.util.*;

/**
 * Concept.java
 *
 *
 * Created: Mon Mar 19 22:42:50 2001
 *
 * @author Cengiz Gunay
 * @version
 */

public interface Concept {
    /**
     * Attaches the presynaptic neuroid to <code>concept</code>.
     *
     * @param neuroid a <code>Neuroid</code> value
     */
    void attach(Neuroid neuroid) throws ConceptSaturatedException;

    /**
     * Detaches the presynaptic neuroid from <code>concept</code>.
     *
     * @param neuroid a <code>Neuroid</code> value
     */
    void detach(Neuroid neuroid);

    
    /**
     * Fire the neuroids associated with the <code>Concept</code>. 
     * TODO: 
     */
    void fire();

    /**
     * Returns the conceptSet that constitutes this concept
     *
     * @return a <code>Set</code> value
     */
    Set getConceptSet();
}// Concept
