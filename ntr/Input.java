package Base;

// $Id$
/**
 * Interface for devices to provide input to the network.
 * 
 *
 * Created: Sat Mar 31 20:07:11 2001
 *
 * @author Cengiz Gunay
 * @version
 */

public interface Input  {
    /**
     * Fires the device, the recipients are notified.
     * @see Neuroid#fire
     */
    void fire();
    
}// Input
