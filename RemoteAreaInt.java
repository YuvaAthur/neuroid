import java.rmi.*;
import java.util.*;

/**
 * RemoteAreaInt.java
 * Distributed extensions to <code>Area</code>
 * @see Area
 *
 * Created: Fri Dec  1 02:52:35 2000
 *
 * @author Cengiz Gunay
 * @version
 */

public interface RemoteAreaInt extends Remote {
    Vector createRandomSynapses(int numberOfSynapses) throws RemoteException;    
    int getNumberOfNeuroids() throws RemoteException;
    int getReplication() throws RemoteException;
    void step() throws RemoteException;
    void connectToArea(Object destArea) throws RemoteException;
}// RemoteAreaInt
