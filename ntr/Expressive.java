
package neuroidnet.ntr;

// $Id$
/**
 * Defines an interface on how an entity should express its identity and state.
 *
 * <p>Created: Wed May  1 16:38:56 2002
 * <p>Modified: $Date$
 *
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version $Revision$ for this file.
 */

public interface Expressive  {
    /**
     * Give enough information on the entity for identification.
     *
     * @return a <code>String</code> value
     */
    String toString();

    /**
     * In addition to <code>toString()</code> contents,
     * give transient properties of the entity without
     * the static properties.
     *
     * @return a <code>String</code> value
     * @see #toString
     */
    String getStatus();

    /**
     * In addition to <code>getStatus()</code> contents,
     * give all static properties also.
     *
     * @return a <code>String</code> value
     * @see #getStatus
     */
    String getProperties();
}// Expressive
