import java.rmi.*;

/**
 * RemoteSynapseInt.java
 * Distributed extensions to Synapse
 * @see Synapse
 * 
 * Created: Fri Dec  1 02:36:25 2000
 *
 * @author Cengiz Gunay
 * @version
 */

public interface RemoteSynapseInt extends Remote {
    void receiveSpike() throws RemoteException;
    Synapse getLocalSynapse() throws RemoteException;
}// RemoteSynapseInt
