package neuroidnet.remote;

import neuroidnet.ntr.*;

// $Id$
/**
 * Distributed extensions of <code>neuroidnet.ntr.Synapse</code>.
 * 
 * <p>Created: Fri Dec  1 02:36:25 2000
 *
 * @see neuroidnet.ntr.Synapse
 * @author Cengiz Gunay
 * @version $Revision$
 */

public interface SynapseInt extends java.rmi.Remote {
    void receiveSpike() throws java.rmi.RemoteException;
    //Synapse getLocalSynapse() throws java.rmi.RemoteException;
}// remote.SynapseInt
