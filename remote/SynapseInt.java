package Remote;
import Base.*;
import Remote.*;
//import java.rmi.*;

/**
 * Remote.SynapseInt.java
 * Distributed extensions to Synapse
 * @see Synapse
 * 
 * Created: Fri Dec  1 02:36:25 2000
 *
 * @author Cengiz Gunay
 * @version
 */

public interface SynapseInt extends java.rmi.Remote {
    void receiveSpike() throws java.rmi.RemoteException;
    Synapse getLocalSynapse() throws java.rmi.RemoteException;
}// Remote.SynapseInt
