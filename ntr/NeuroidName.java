
package neuroidnet.ntr;

// $Id$
/**
 * Identifier for accessing a neuroid in a network.
 *
 * <p>Created: Wed May 15 14:06:47 2002
 * <p>Modified: $Date$
 *
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version $Revision$ for this file.
 */

public class NeuroidName  {

    final String areaName;
    
    /**
     * Get the value of areaName.
     * @return value of areaName.
     */
    public String getAreaName() {
	return areaName;
    }
    
    final int neuroidId;
    
    /**
     * Get the value of neuroidId.
     * @return value of neuroidId.
     */
    public int getNeuroidId() {
	return neuroidId;
    }

    public NeuroidName (String areaName, int neuroidId) {
	this.areaName = areaName;
	this.neuroidId = neuroidId;
    }
    
}// NeuroidName
