package Remote;
import Base.*;
import Remote.*;
//import java.rmi.*;
import java.util.*;

/**
 * Remote.AreaInt.java
 * Distributed extensions to <code>Area</code>
 * @see Area
 *
 * Created: Fri Dec  1 02:52:35 2000
 *
 * @author Cengiz Gunay
 * @version
 */

public interface AreaInt extends java.rmi.Remote {
    Vector createRandomSynapses(Neuroid fromNeuroid, int numberOfSynapses)
	throws java.rmi.RemoteException; 
    Vector createArbitrarySynapses(Neuroid fromNeuroid, int numberOfSynapses)
	throws java.rmi.RemoteException; 
    int getNumberOfNeuroids() throws java.rmi.RemoteException;
    int getReplication() throws java.rmi.RemoteException;
    void step() throws java.rmi.RemoteException;
    void connectToArea(Object destArea) throws java.rmi.RemoteException;
}// Remote.AreaInt
