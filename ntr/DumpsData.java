package Base;

// $Id$
/**
 * DumpsData.java
 *
 *
 * <p>Created: Fri May 18 01:55:06 2001
 * <p>Modified: $Date$
 *
 * @author Cengiz Gunay
 * @version $Revision$ for this file.
 */

public interface DumpsData  {
    /**
     * Creates a Matlab script depicting the activity of the element on a graph
     * or simply exports the information in matlab style variables.
     * 
     * @return a <code>String</code> value of matlab code
     */
    public String dumpData ();
    
}// DumpsData
