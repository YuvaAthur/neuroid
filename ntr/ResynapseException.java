package Base;
import Base.*;
import Remote.*;

/**
 * ResynapseException.java
 * Exception thrown in case two synaptic boutons of an axon end up
 * on the same postsynaptic neuroid.
 *
 * Created: Sun Mar 11 01:42:59 2001
 *
 * @author Cengiz Gunay
 * @version
 */

public class ResynapseException extends Exception {
    public ResynapseException (String msg) {
	super(msg);
    }
    
}// ResynapseException
