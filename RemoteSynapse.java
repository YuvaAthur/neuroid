import java.rmi.*;
import java.rmi.server.*;

/**
 * RemoteSynapse.java
 *
 *
 * Created: Fri Dec  1 02:34:03 2000
 *
 * @author Cengiz Gunay
 * @version
 */

public class RemoteSynapse extends Synapse
    implements RemoteSynapseInt {
    Synapse localSynapse;
    
    /**
       * Get the value of localSynapse.
       * @return value of localSynapse.
       */
    public Synapse getLocalSynapse() {return localSynapse;}
    
    /**
       * Set the value of localSynapse.
       * @param v  Value to assign to localSynapse.
       */
    public void setLocalSynapse(Synapse  v) {this.localSynapse = v;}
    
    public RemoteSynapse (Neuroid destNeuroid, double timeConstantM,
			  double timeConstantS, boolean isInhibitory)
	throws RemoteException {
	super(destNeuroid, timeConstantM, timeConstantS, isInhibitory);
	UnicastRemoteObject.exportObject(this);
    }

/*    public void receiveSpike() throws RemoteException {
	localSynapse.receiveSpike();
    }*/

}// RemoteSynapse
