package periphery;
import java.util.*;
import Base.*;

// $Id$
/**
 * Neuroid that is not contained in a SensoryArea and which does not listen to its inputs.
 * It is excited by sensory inputs, by call to its <code>fire()</code> method.
 * It is connected to a replicated neuroids in a destination area.
 * It has a SensoryConcept associated with it.
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

public class SensoryNeuroid extends Base.Neuroid {
    /**
     * Create neuroid and associate a new <code>SensoryConcept</code> with it.
     * Connects <em>arbitrarily</em> to a number of neuroids at the destination area.
     * 
     * @see SensoryConcept
     * @see Base.Area#createArbitrarySynapses
     * @param sensoryArea Where to put this neuroid.
     * @param conceptName Name.
     * @param destArea the destination area to project axons.
     */
    public SensoryNeuroid(Base.Area sensoryArea, Base.Area destArea, String conceptName) {
	super(sensoryArea); 

	// Create concept
	concept = new SensoryConcept(area.network, conceptName);
	concept.attach(this);
	
	mode.setState(Mode.UM);

	// Create connections to destArea
	AxonArbor synapses;
	Synapse synapseTemplate = // TODO: Fix timeConstantS??
	    new Synapse(null, null, area.timeConstantM, area.deltaT, false, 0);
	synapses = destArea.createArbitrarySynapses(synapseTemplate, this,
						    destArea.getReplication());
	/* this one is for RemoteAreas
	try {
	    synapses = destArea.createArbitrarySynapses(null, destArea.getReplication());
	} catch (java.rmi.RemoteException e) {
	    e.printStackTrace();
	    throw new RuntimeException("Cannot access area.createArbitrarySynapses");
	}*/
	area.addAxon(this, synapses);
    }
    
    /**
     * N/A
     */
    public void step() {
	//throw new Error("Fatal: This neuroid cannot be step()ped.");
    }

    
}// SensoryNeuroid
