package neuroidnet.periphery;
import neuroidnet.ntr.*;
import neuroidnet.remote.*;

// $Id$
/**
 * ConceptSaturatedException.java
 * Exception thrown from ArtificialConcept.attach() whenever a Neuroid is
 * recruited tries to attach to a Concept which has already Area.replication
 * members.
 *
 * <p>Created: Tue Sep 18 23:24:35 2001
 * <p>Modified: $Date$
 *
 * @author Cengiz Gunay
 * @version $Revision$ for this file.
 * @see ArtificialConcept#attach
 */

public class ConceptSaturatedException extends Exception {
    public ConceptSaturatedException (String msg) {
	super(msg);
    }

}
