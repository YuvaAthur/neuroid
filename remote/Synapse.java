package Remote;
import Base.*;
import Remote.*;
//import java.rmi.*;
import java.rmi.server.*;

/**
 * Remote.Synapse.java
 *
 *
 * Created: Fri Dec  1 02:34:03 2000
 *
 * @author Cengiz Gunay
 * @version
 */

public class Synapse extends Base.Synapse
    implements Remote.SynapseInt {
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
    
    public Synapse (Neuroid destNeuroid, double timeConstantM,
			  double timeConstantS, boolean isInhibitory)
	throws java.rmi.RemoteException {
	super(destNeuroid, timeConstantM, timeConstantS, isInhibitory);
	UnicastRemoteObject.exportObject(this);
    }

/*    public void receiveSpike() throws java.rmi.RemoteException {
	localSynapse.receiveSpike();
    }*/

}// Remote.Synapse
