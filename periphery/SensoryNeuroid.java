package neuroidnet.periphery;

import neuroidnet.ntr.*;

import java.util.*;

// $Id$
/**
 * Neuroid that is not contained in a SensoryArea and which does not listen to its inputs.
 * It is excited by sensory inputs, by call to its <code>fire()</code> method.
 * It is connected to a replicated neuroids in a destination area.
 * It has a SensoryConcept associated with it.
 * <p>Shouldn't be a SRMNeuroid, should have a SRMMode. Should have special mode with only state.
 * @see Neuroid#fire()
 * @see SensoryConcept
 * @see SensoryNeuroid
 * 
 * <p>Created: Sat Mar 31 20:35:53 2001
 * <p>Modified: $Date$
 * 
 * @author <a href="mailto:">Cengiz Gunay</a>
 * @version $Revision$ for this file.
 */

public class SensoryNeuroid extends SRMNeuroid {
    /**
     * Create neuroid and associate a new <code>SensoryConcept</code> with it.
     * Connects <em>arbitrarily</em> to a number of neuroids at the destination area.
     * 
     * @see SensoryConcept
     * @see ntr.Area#addArbitrarySynapses
     * @param sensoryArea Where to put this neuroid.
     * @param conceptName Name.
     * @param destArea the destination area to project axons.
     */
    public SensoryNeuroid(Area sensoryArea, Area destArea, String conceptName) {
	super(sensoryArea); 

	// Create concept
	concept = new SensoryConcept(area.network, conceptName);
	try {
	    concept.attach(this);	     
	} catch (ConceptSaturatedException e) {
	    throw new RuntimeException("New concept is already full?");
	} // end of try-catch
	
	mode.setState(Mode.UM);

	// Create connections to destArea
	AxonArbor synapses;
	Synapse synapseTemplate = // TODO: Fix timeConstantS??
	    new Synapse(null, null, area.timeConstantM, area.deltaT, false, 0);
	destArea.addArbitrarySynapses(synapseTemplate, this,
				      destArea.getReplication());
	/* this one is for RemoteAreas
	try {
	    synapses = destArea.addArbitrarySynapses(null, destArea.getReplication());
	} catch (java.rmi.RemoteException e) {
	    e.printStackTrace();
	    throw new RuntimeException("Cannot access area.addArbitrarySynapses");
	}*/
	//area.addAxon(this, synapses);
    }
    
    /**
     * N/A
     */
    public void step() {
	//throw new Error("Fatal: This neuroid cannot be step()ped.");
    }

    
}// SensoryNeuroid
