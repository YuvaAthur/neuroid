package neuroidnet.remote;
import neuroidnet.*;
import neuroidnet.ntr.*;

//import java.rmi.*;
import java.util.*;

/**
 * Distributed extensions to <code>Area</code>
 * @see Area
 *
 * Created: Fri Dec  1 02:52:35 2000
 *
 * @author Cengiz Gunay
 * @version
 */

public interface AreaInt extends java.rmi.Remote {
    void addRandomSynapses(neuroidnet.ntr.Synapse destSynapseTemplate, Neuroid srcNeuroid,
				   int numberOfSynapses)
	throws java.rmi.RemoteException;
    void addArbitrarySynapses(neuroidnet.ntr.Synapse destSynapseTemplate,
				      Neuroid srcNeuroid,
				      int numberOfSynapses) 
	throws java.rmi.RemoteException; 
    int getNumberOfNeuroids() throws java.rmi.RemoteException;
    int getReplication() throws java.rmi.RemoteException;
    void step() throws java.rmi.RemoteException;
    void connectToArea(AreaInt destArea, double timeConstantS,
		       double delay, double nuBoost)
	throws java.rmi.RemoteException;
}// remote.AreaInt
