package neuroidnet.ntr;

import edu.ull.cgunay.utils.plots.*;
import neuroidnet.periphery.*;
import edu.ull.cgunay.utils.*;
//import neuroidnet.remote.*;

import java.lang.*;
import java.util.*;
import java.text.*;
import java.io.*;
//import java.rmi.*;

// $Id$
/**
 * Abstract neuroid entity.  Instances are contained in
 * <code>Area</code>s.
 *
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version $Revision$ for this file
 * @since 1.0
 * @see Area
 */
abstract public class Neuroid implements Simulation, Input, Expressive {
    
    /**
     * Membrane potential.
     */
    double potential;

    /**
     * List of incoming synapses.
     */
    protected Dendrite synapses = new Dendrite();
    
    /**
     * Get the value of synapses.
     * @return value of synapses.
     */
    public Dendrite getSynapses() {
	return synapses;
    }
    
    /**
     * Last firing time of this Neuroid.
     */
    double timeLastFired; // TODO: make time a class?
    
    /**
     * Get the value of timeLastFired.
     * @return value of timeLastFired.
     */
    public double getTimeLastFired() {
	return timeLastFired;
    }
    
    /**
     * Parent Area.
     */
    protected Area area;
    
    /**
     * Get the value of area.
     * @return value of area.
     */
    public Area getArea() {return area;}
    
    /**
       * Set the value of area.
       * @param v  Value to assign to area.
       */
    public void setArea(Area  v) {this.area = v;}
    
    /**
     * Associated concept if neuroid has memorized anything.
     * @see Concept
     */
    protected Concept concept = null;
    
    /**
     * Get the value of concept.
     * @return value of concept.
     */
    public Concept getConcept() {
	return concept;
    }
    
    /**
     * Sequence of <code>Neuroid</code> in the array held in <code>Area</code>.
     * Good for representation.
     * Used to override Object.hashCode() and don't pass around this id, but 
     * hascode is dynamic and changes at every runtime, leaving us with a volatile identifier.
     * Oh, well. Set in <code>Area.addNeuroid()</code>
     * @see Area#neuroids
     * @see Area#addNeuroid
     */
    final int id; 
    
    /**
     * Get the value of id.
     * @return value of id.
     */
    public int getId() {
	return id;
    }
  
    /**
     * Represents the state and dynamic parameters of the neuroid.
     */
    protected Mode mode;
    
    /**
     * Get the value of mode.
     * @return value of mode.
     */
    public Mode getMode() {
	return mode;
    }
    
    /**
     * Set the value of mode and connects any profilers if watched.
     * @param v  Value to assign to mode.
     */
    public void setMode(Mode  v) {
	this.mode = v;
	try {
	    profile.getModeProfile().connectTo(mode, new Double(area.time)); 
	} catch (NullPointerException e) { } // it's ok, then don't do it.
    }    

    /**
     * If set, outputs debugging info about this neuroid.
     */
    boolean debug = false;

    /**
     * Get the value of debug.
     * @return value of debug.
     */
    public boolean isDebug() {
	return debug;
    }
    
    /**
     * Set the value of debug.
     * @param v  Value to assign to debug.
     */
    public void setDebug(boolean  v) {
	this.debug = v;
    }

    /**
     * If set, the neuroid saves informatoin about its state changes.
     */
    protected boolean watch = false;
    
    /**
     * Get the value of watch.
     * @return value of watch.
     */
    public boolean isWatch() {
	return watch;
    }
    
    /**
     * Set the value of watch. if watched, create a <code>NeuroidProfile</code>
     * object.
     * @param v  Value to assign to watch.
     */
    public void setWatch(boolean  v) {
	this.watch = v;
	if (watch) 
	    profile = new NeuroidProfile(this);
    }

    /**
     * List of times that the neuroid fired.
     * Redundant with the <code>spikeTrain</code> defined in <code>Synapse</code>.
     * @see Synapse#spikeTrain
     */
    Vector spikeTrain = new Vector(); // TODO: make time a class?
    
    /**
     * Get the value of spikeTrain.
     * @return value of spikeTrain.
     */
    public Vector getSpikeTrain() {
	return spikeTrain;
    }
    
    /**
     * Set the value of spikeTrain.
     * @param v  Value to assign to spikeTrain.
     */
    public void setSpikeTrain(Vector  v) {
	this.spikeTrain = v;
    }

    NeuroidProfile profile;
    
    /**
     * Get the value of profile.
     * @return value of profile.
     */
    public NeuroidProfile getProfile() {
	return profile;
    }
    
    /**
     * Set the value of profile.
     * @param v  Value to assign to profile.
     */
    public void setProfile(NeuroidProfile  v) {
	this.profile = v;
    }

    /**
     *  and connects to <code>area</code>.
     *
     */
    public Neuroid(Area area) {
	this.area = area;

	id = area.addNeuroid(this);
	//init();
    }

    /**
     * N/A
     */
    final public void init() { }

    /*
     * Returns Address of the neuroid.
     * OBSOLETE!
     * @deprecated It might come back, though.
     * @return an <code>Address</code> value
     * @see Neuroid.Address
     */
/*    public Address getAddress() {
	return new Address(area.id, id);
    }*/
    
    /*
     * Method of java.lang.Object that return codes for hash table entries.
     * OBSOLETE: why do we need this?
     * @return an <code>int</code> value
     */
/*    public int hashCode() {
	return id;
    }
*/
    /*
     * Method of java.lang.Object that is used in many utils.
     * OSOLETE: why do we need this?
     * @param obj Another Neuroid.
     * @see Neuroid
     * @return True if two Neuroids have the same <code>id</code>.
     * @see Neuroid#id
     */
/*    public boolean equals(Object obj) {
	return id == ((Neuroid) obj).id;
    }
*/

    /**
     * Fires the neuroid. Neuroid contact container Area to propagate the spike along its axon.
     * Adds an entry to <code>spikeTrain</code> if being watched.
     * @see Area#fireNeuroid
     * @see #watch
     */
    public void fire() {
	System.out.println("Fire at t=" + Network.numberFormat.format(area.time) + " " +
			   getStatus());
	timeLastFired = area.time;
	area.fireNeuroid(this);
	if (watch) 
	    spikeTrain.add(new Double(timeLastFired)); 
    }

    /**
     * Update state of the <code>Neuroid</code>.
     * Use the UMT algorithm by default, though behavior will depend
     * on <code>Synapse</code> parameters.
     * @see Synapse
     */
    public void step() {
	calculatePotential();

	mode.step();
    }

    /**
     * Calculate the <code>potential</code> here.
     * @see #potential
     */
    abstract protected void calculatePotential();

    /**
     * Iterator task class which scans synapses of the neuroid.
     * 
     * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
     * @version 1.0
     * @since 1.0
     * @see edu.ull.cgunay.utils.Task
     */
    abstract class SynapseActivityTask implements edu.ull.cgunay.utils.TaskWithReturn {
	//Neuroid toplevel = Neuroid.this;

	SynapseActivityTask() {	}

	/**
	 * Initiates the iteration over synapses of the enclosing Neuroid.
	 *
	 */
	public void iterate() {
	    UninterruptedIteration.loop(synapses, this); 
	}

	/**
	 * Calls <code>potentiatedSynapse</code> and <code>silentSynapse</code>
	 * deciding by scanning <code>Synapse</code>s.
	 *
	 * @param o an <code>Object</code> value
	 * @see Synapse
	 * @see #potentiatedSynapse
	 * @see #silentSynapse
	 */
	public void job(Object o) {
	    Synapse s = (Synapse) o;
	    if (s.isPotentiated())
		potentiatedSynapse(s);
	    else 
		silentSynapse(s);
	} 

	/**
	 * Called for each potentiated synapse.
	 *
	 * @param s a <code>Synapse</code> value
	 */
	abstract void potentiatedSynapse(Synapse s);

	/**
	 * Called for each non-potentiated synapse.
	 *
	 * @param s a <code>Synapse</code> value
	 */
	abstract void silentSynapse(Synapse s);
    }

    /**
     * Create new concept.
     * Trace back synapses that made this neuroid go to UM mode to find concept set.
     * Then look for associated concept in conceptArea,
     * otherwise create new Concept using this concept set.
     * <!-- In case concept is saturated, then the neuroid is reset() -->
     * <p>TODO: destroy old <code>concept</code> if it exists.
     * @exception ConceptSaturatedException 
     * @see ConceptArea
     * @see Concept
     * @see ConceptSaturatedException
     */
    void makeConcept() throws ConceptSaturatedException {

	// Loop over synapses and save their concepts in conceptSet
	HashSet conceptSet = (HashSet) new SynapseActivityTask() {
		HashSet conceptSet;

		public void iterate() {
		    conceptSet = new HashSet();
		    super.iterate();
		}

		public void job(Object o) {
		    super.job(o);	// main job
		}

		void potentiatedSynapse(Synapse s) {
		    conceptSet.addAll(s.srcNeuroid.concept.getConceptSet());
		}

		void silentSynapse(Synapse s) { }

		// Collect the result of the iteration over the synapses.
		public Object getValue() { iterate(); return conceptSet; }
	    }.getValue();
	
	// Beware: local variable
	Concept concept = (Concept) area.network.conceptArea.get(conceptSet); 

	try {
	    concept.equals(concept); // redundant operation to raise exception
	} catch (NullPointerException e) { 
	    concept = new ArtificialConcept(area.network, conceptSet);
	} 

	// An exception should be received in case the replication factor is exceeded.
	// refractory period is enabled without exhibiting spiking to suppress re-activation.
	// TODO2: Later make global inhibitory connections limit this number.
	// 	  One might randomly distribute the timeConstantS with deviation param
	//	  and the global inhibitory neuroid will suppress late concepts even
	// 	  from going into UM
	concept.attach(this);

	// if no exception was raised
	this.concept = concept;
    }

    /**
     * Add <code>synapse</code> to <code>synapses</code> and return index to become
     * the id of the <code>Synapse</code>. Analogous to <code>Area.addNeuroid()</code>
     *
     * @param synapse a <code>Synapse</code> to add to <code>synapses</code>
     * @return an <code>int</code> value, the index of new synapse in <code>synapses</code>
     * @see Area#addNeuroid
     */
    public int addSynapse(Synapse synapse) {
	synapses.add(synapse);
	return synapses.indexOf(synapse);
    }

    /**
     * Identify the neuroid with its <code>id</code> and <code>area</code>.
     *
     * @return a <code>String</code> value
     */
    public String toString() {
	String name = getClass().getName();
	return name.substring(name.lastIndexOf('.') + 1) + " #" + id + " (in " + area + ")";
    }

    /**
     * Output of <code>toString()</code> plus potential,
     * mode status, last fire time, and concept name.
     *
     * @return a <code>String</code> value
     */
    public String getStatus() {
	return
	    this + " (" + mode.getStatus() + "), u = " +
	    Network.numberFormat.format(potential) + 
	    ", fired = " + Network.numberFormat.format(timeLastFired) + 
	    (concept != null ? ", " + concept : "");
    }

    /**
     * Describe in higher detail. List all contained synapses.
     *
     * @return a <code>String</code> value
     */
    public String getProperties() {
	return
	    this.getStatus() +
	    new StringTask(" {\n", "\n}") {
		public void job(Object o) {
		    Synapse s = (Synapse)o;
		    super.job("\t" + s.nameString() + " with " + s.statusString() +
			      " (from " + s.srcNeuroid.getStatus() + ")\n");
		}
	    }.getString(synapses);
    }

    /**
     * Dump synaptic activity to output (matlab format). Called by:
     * TODO: optionally in gnuplot format?
     * @deprecated Use plots and the profile gathering system.
     * @see ConceptArea#dumpData
     */
    public String dumpData() {
	return null /*new StringTask() {
		// spike lists from different synapses separated by space
		public void job(Object o) {
		    this.retval += ((Synapse)o).dumpData() + " "; 
		}
	    }.getString(synapses)*/;
    }

    /**
     * Address of a neuroid, conjunction of container area id and
     * neuroid id within the area.
     *
     * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
     * @version 1.0
     * @since 1.0
     */
/*    public class Address {
	int areaId, neuroidId;
    }
*/
    /**
     * Modes that a neuron can have.
     * <p>TODO: Make this a hierarchy of classes in a subpackage called umt
     *
     * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
     * @version 1.0
     * @since 1.0
     */
    abstract protected class Mode extends Profilable implements Simulation, Expressive {

	/**
	 * Dummy constructor.
	 *
	 */
	Mode() { }

	Mode(int state, double threshold) {
	    this.state = state;
	    this.threshold = threshold;
	}
	
	/**
	 * State of neuroid, can only take values defined here.
	 * TODO: Use byte?  
	 */
	int state;
	
	/**
	   * Get the value of state.
	   * @return value of state.
	   */
	public int getState() {return state;}
	
	/**
	   * Set the value of state.
	   * @param v  Value to assign to state.
	   */
	public void setState(int  v) {
	    this.state = v;
	    setChanged();
	}
	
	/**
	 * Threshold of the neuroid.
	 */
	double threshold;
	
	/**
	   * Get the value of threshold.
	   * @return value of threshold.
	   */
	public double getThreshold() {return threshold;}
	
	/**
	 * Set the value of threshold.
	 * @param v  Value to assign to threshold.
	 */
	public void setThreshold(double  v) {
	    this.threshold = v;
	    setChanged(); 
	}	

	public void init() { }
	
	/**
	 * Strip fully qualified class name to leave only the class name.
	 *
	 * @return a <code>String</code> value
	 */
	public String toString() {
	    String name = getClass().getName();
	    return name.substring(name.lastIndexOf('.') + 1);
	}

	/**
	 * <code>toString()</code> plus state, threshold.
	 *
	 */
	public String getStatus() {
	    return
		this + ": state = " + /*stateList[*/ state /*]*/ +
		", T = " + Network.numberFormat.format(threshold);
	}

	/**
	 * Nothing special.
	 *
	 * @return a <code>String</code> value
	 */
	public String getProperties() {
	    return getStatus();
	}

	public double doubleValue() {
	    throw new RuntimeException("Not applicable to this object");
	} 

	/**
	 * Returns a reference to the enclosing <code>Neuroid</code>.
	 *
	 * @return a <code>Neuroid</code> value
	 */
	public Neuroid getNeuroid() {
	    return Neuroid.this;
	}

	abstract class State implements Simulation {

	    /**
	     * The name of the state.
	     */
	    String name;

	    /**
	     * Dummy constructor, calls <code>init()</code>. Obsolete?
	     * @see Simulation#init
	     */
	    public State() { init(); }

	    /**
	     * Assigns state name.
	     *
	     * @param name a <code>String</code> value
	     */
	    public State(String name) { this.name = name; }

	    /**
	     * Dummy.
	     */
	    public void init() {}

	    /**
	     * Returns state name.
	     * @see #name
	     * @return a <code>String</code> value
	     */
	    public String toString() {
		return "LState: " + name;
	    }
	}
    }
}
