
package Base;

// $Id$
/**
 * DebuggerInterface.java
 *
 *
 * <p>Created: Fri Dec  7 18:38:17 2001
 * <p>Modified: $Date$
 *
 * @author <a href="mailto:">Cengiz Gunay</a>
 * @version $Revision$ for this file.
 */

public interface DebuggerInterface  {
    Area getArea(String name) throws NameNotFoundException; 
    Neuroid getNeuroid(Area area, int neuroidId);
    Neuroid getNeuroid(String areaName, int neuroidId) throws NameNotFoundException;
    void setWatch(Neuroid neuroid);
}// DebuggerInterface
