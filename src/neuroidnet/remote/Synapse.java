package neuroidnet.remote;

import neuroidnet.ntr.*;

import java.rmi.server.*;

// $Id$
/**
 * An augmented remote synapse definition.
 *
 * <p>Created: Fri Dec  1 02:34:03 2000
 *
 * @author Cengiz Gunay
 * @version $Revision$
 * @deprecated See <code>neuroidnet.ntr.Synapse</code>
 */

public class Synapse extends neuroidnet.ntr.Synapse
    implements SynapseInt {
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
    
    public Synapse (Neuroid srcNeuroid, Neuroid destNeuroid, double timeConstantM,
		    double timeConstantS, boolean isInhibitory, double delay)
	throws java.rmi.RemoteException {
	super(srcNeuroid, destNeuroid, timeConstantM, timeConstantS, isInhibitory, delay);
	UnicastRemoteObject.exportObject(this);
    }

/*    public void receiveSpike() throws java.rmi.RemoteException {
	localSynapse.receiveSpike();
    }*/

}// remote.Synapse
