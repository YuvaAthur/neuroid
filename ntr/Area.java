package Base;
import Base.*;
import Remote.*;
import java.lang.*;
import java.util.*;
import java.text.*;
import Utils.*;

/**
 * Entity <code>Area</code> that holds neurons and keeps track of time.
 *
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version 1.0
 * @since 1.0
 */

public class Area implements Runnable, Remote.AreaInt {

    /**
     * Name of the <code>Area</code> for identification purposes.
     */
    String name;

    /**
     * Parent network.
     * OBSOLETE: Excessive connection!
     */
    //Network network;

    /**
     * Time of the area.
     */
    public double time; // TODO: make time a class? Don't make it public! Synchronize?

    /**
     * Increment of time for algorithms.
     * Proxy for <code>deltaT</code> in <code>network</code>.
     * @see Network#deltaT
     */
    public double deltaT;

    /**
     * Id tag of the area.
     */
    public int id; // identification tag

    /**
     * Number of <code>Neuroid</code>s contained in <code>Area</code>.
     * TODO: should be obsolete, redundant info with size of <code>neuroids</code> vector.
     * @see #neuroids
     * @see Neuroid
     */
    int numberOfNeuroids;
    
    /**
       * Get the value of numberOfNeuroids.
       * @return value of numberOfNeuroids.
       */
    public int getNumberOfNeuroids() {return numberOfNeuroids;}
    
    /**
       * Set the value of numberOfNeuroids.
       * @param v  Value to assign to numberOfNeuroids.
       */
    public void setNumberOfNeuroids(int  v) {this.numberOfNeuroids = v;}
    
    /**
     * The replication factor, i.e. the number of <code>Neuroid<code>s allocated for each concept.
     */
    int replication;

    /**
       * Get the value of replication.
       * @return value of replication.
       */
    public int getReplication() {return replication;}
    
    /**
       * Set the value of replication.
       * @param v  Value to assign to replication.
       */
    public void setReplication(int  v) {this.replication = v;}
    
    /**
     * Oscillation period of <code>Neuroid</code>s.
     * @see Neuroid
     */
    double period;

    /**
     * List of <code>Neuroid</code>s contained in <code>Area</code>.
     * TODO: Fix access modifier from public, becuase of peripherals..
     * @see Neuroid
     */
    public Vector neuroids;

    /**
     * 
     */
    Neuroid inhibitoryInterNeuroid;

    /**
     * Hash table holding the white matter, e.g. the axons of neuroids residing in this
     * <code>Area</code> connecting to <code>Synapse</code>s of other <code>Neuroid</code>s.
     * Returns a <code>Vector</code> of <code>Synapses</code> for every <code>Neuroid</code> key.
     * @see Synapse
     * @see Neuroid
     */
    Hashtable axons = new Hashtable();

    /**
     * Lock variable showing the thread is busy doing the calculations.
     * @see Area#run
     * @see Area#step
     */
    volatile Boolean isCalculating = new Boolean(false);

    /**
     * Flag showing incoming requets to step().
     * @see Area#run
     * @see Area#step
     */
    volatile boolean stepRequested = false;

    /**
     * Thread to do the actual calculations.
     */
    Thread thread;

    /**
     * Count of concept allocations made (via call to createArbitrarySynapses)
     * in this <code>Area</code>.
     * @see #createArbitrarySynapses
     * @see Peripheral.Concept
     */
    int conceptCount = 0;

    /*
     * The concept that this neuroid belongs to. Mistake?? should be in neuroid
     * @see Concept
     */
    // Concept concept;

    /**
     * Pointer to parent network
     * TODO: Fix access modifier from public, problem with SensoryNeuroid
     */
    public Network network;

    /**
     * True if the area contains a inhibitroy inter-neuroid.
     */
    boolean inhibInter;

    /**
     * Membrane time constant of all neurons in this area.
     */
    public double timeConstantM;

    /**
     * The threshold to change a Neuroid from AM to AM1 mode.
     * @see Neuroid.step
     */
    double activationThreshold;
    
    /**
       * Get the value of activationThreshold.
       * @return value of activationThreshold.
       */
    public double getActivationThreshold() {return activationThreshold;}
    
    /**
       * Set the value of activationThreshold.
       * @param v  Value to assign to activationThreshold.
       */
    public void setActivationThreshold(double  v) {this.activationThreshold = v;}
    

    /**
     * Constructor for plain Area (no inhibitory interneuron). Calls <code>init()</code>.
     * @see #init()
     * @param network a <code>Network</code> value
     * @param name a <code>String</code> value
     * @param numberOfNeuroids an <code>int</code> value
     * @param replication an <code>int</code> value
     * @param deltaT a <code>double</code> value
     * @param period a <code>double</code> value
     * @param threshold a <code>double</code> value
     */
    public Area(Network network, String name, int numberOfNeuroids,
		int replication, double period, double threshold, double timeConstantM,
		double refractoryTimeConstant) {
	init(network, name, numberOfNeuroids, replication, period, threshold, false,
	     timeConstantM, refractoryTimeConstant);
    }

    /**
     * Constructor with option to add inhibitory inter-neuron. Calls <code>init()</code>.
     * @see #init()
     *
     * @param network a <code>Network</code> value
     * @param name a <code>String</code> value
     * @param numberOfNeuroids an <code>int</code> value
     * @param replication an <code>int</code> value
     * @param deltaT a <code>double</code> value
     * @param period a <code>double</code> value
     * @param threshold a <code>double</code> value
     * @param inhibInter a <code>boolean</code> value
     */
    public Area(Network network, String name, int numberOfNeuroids, int replication,
		double period, double threshold, boolean inhibInter, double timeConstantM,
		double refractoryTimeConstant) {
	init(network, name, numberOfNeuroids, replication, period,
	     threshold, inhibInter, timeConstantM, refractoryTimeConstant);
    }

    /**
     * Called by constructors.
     * Creates a new <code>Area</code> instance with <code>numberOfNeuroids</code> of
     * <code>Neuroids</code>.
     * <p>TODO: If a allocate-on-demand approach is used for Neuroids none
     * should be allocated at this time.
     * <p>TODO: Automatically add areas to network?
     * @see Neuroid
     *
     * @param network a <code>Network</code> value
     * @param name a <code>String</code> value
     * @param numberOfNeuroids an <code>int</code> value
     * @param replication an <code>int</code> value
     * @param deltaT a <code>double</code> value
     * @param period a <code>double</code> value
     * @param threshold a <code>double</code> value
     * @param inhibInter a <code>boolean</code> value
     */
    void init(Network network, String name, int numberOfNeuroids, int replication,
	      double period, double threshold, boolean inhibInter, double timeConstantM,
	      double refractoryTimeConstant) {
	this.numberOfNeuroids = numberOfNeuroids;
	this.replication = replication;
	this.deltaT = network.deltaT;
	this.period = period;
	this.name = name;
	this.network = network;
	this.inhibInter = inhibInter;
	this.timeConstantM = timeConstantM;

	// Neuroids in Area (one for the inhib. inter-neuron)
	neuroids = new Vector(numberOfNeuroids+(inhibInter?1:0)); 

	if (inhibInter) {
	    // Add inhibitory inter-neuron: one neuron that takes input from all neuroids and
	    // projects to all. Threshold is fixed to fire above 2*replication inputs
	    // weights and threshold should *not* be modified? refraction?
	    inhibitoryInterNeuroid = new Neuroid(this, /*numberOfNeuroids,*/ /*2**/replication*0.9, refractoryTimeConstant);
	    // Create concept
	    (new periphery.SensoryConcept(network, "Area: " + name + " inhibitory neuroid")).
		attach(inhibitoryInterNeuroid);
	} // end of if (inhibInter)

	// The activation threshold that makes neuroids go from AM to AM1
	// @see Neuroid.step
	activationThreshold = threshold;

	// Instantiate Neuroids
	for (int i = 0; i < numberOfNeuroids; i++)  // Enumerate neuroids
	    new Neuroid(this, /*i,*/ activationThreshold, refractoryTimeConstant);

	// Create a thread to do the step()s
	thread = new Thread(this);
	thread.start();

    }

    /**
     * Adds a neuroid to the <code>Area</code>.
     *
     * @param neuroid a <code>Neuroid</code> value
     */
    public void addNeuroid(Neuroid neuroid) {
	neuroids.add(neuroid);

	if (inhibInter) {
	    Vector inhibitorySynapseVector = new Vector();	

	    // Add inhibitory synapse to neuroid from inhibitoryInterNeuroid
	    // TODO: how do we set the params from outside?
	    inhibitorySynapseVector.add(new Synapse(inhibitoryInterNeuroid, neuroid,
						    0.5, deltaT, true, 0)); // no delay

	    // Add an initial excitory synapse to inhibitoryInterNeuroid from neuroid
	    Vector oneSynapse = new Vector();
	    oneSynapse.add(new Synapse(neuroid,inhibitoryInterNeuroid, 1, deltaT, false, 0)); // no delay
	    axons.put(neuroid, oneSynapse);

	    axons.put(inhibitoryInterNeuroid, inhibitorySynapseVector);
	} // end of if (inhibInter)
    }

    /**
     * Makes connections between this <code>Area</code> and the given <code>destArea</code>.
     * Uses modified connection prbability of rrandom multipartite graphs to determine number
     * of <code>Neuroid</code>s to be connected on destination <code>Area</code>.
     *
     * @see Neuroid
     * @param destArea the <code>Area</code> to which this one is connected.
     */
    public void connectToArea(Remote.AreaInt destArea, double timeConstantS, double delay, double nuBoost) {
	// TODO: don't connect inhibitoryInterNeuroid!
	int destReplication, destNumberOfNeuroids;

	try {
	    destReplication = ((Remote.AreaInt)destArea).getReplication();
	    destNumberOfNeuroids = ((Remote.AreaInt)destArea).getNumberOfNeuroids(); 
	} catch (java.rmi.RemoteException e) {
	    throw new Error("Cannot call Remote.Area methods.");
	}
	
	// Valiant's connection probability for random multipartite graphs 

	double connProb = Math.sqrt((double)nuBoost * destReplication /
				    (destNumberOfNeuroids * replication * replication ));
	// Number of neuroids in the destination to be connected to each neuroid here
	int numberOfConnections = (int) (destNumberOfNeuroids * connProb);

	System.out.println("Conn prob from " + this + " to " + destArea + " is " +
			   Network.numberFormat.format(connProb) + "(" + numberOfConnections +
			   " Neuroids)");

	// Loop for every neuroid in this area
	Object[] p = { destArea,
		       new Integer(numberOfConnections),
		       // SRM parameters: timeConstantM = 1, timeConstantS = deltaT, excitatory 
		       new Synapse(null, null, timeConstantM, timeConstantS,
				   false, delay)};
	Iteration.loop(neuroids.iterator(), new Utils.TaskWithParam() { 
		public void job(Object o, Object[] p) {
		    Neuroid srcNeuroid = (Neuroid) o;
		    Remote.AreaInt _destArea = (Remote.AreaInt) p[0];
		    int _numberOfConnections = ((Integer) p[1]).intValue();
		    Synapse _synapseTemplate = (Synapse) p[2];

		    try {
			AxonArbor axon =
			    _destArea.createRandomSynapses(_synapseTemplate, srcNeuroid,
							   _numberOfConnections); 
			addAxon(srcNeuroid, axon);
		    } catch (java.rmi.RemoteException e) {
			throw new Error("Cannot call Remote.Area methods.");
		    }
		}}, p);
    }

    /**
     * Adds the synapses to the outgoing synapse record of the <code>Area</code>.
     * If synapses associated with <code>srcNeuroid</code> exist,
     * new synapses are just added to them.
     * TO DO: get a remote reference to an AxonArbor in the RemoteArea and put it in hash.
     * @param srcNeuroid the presynaptic <code>Neuroid</code> in this <code>Area</code>.
     * @param synapses the <code>Vector</code> to be associated with <code>srcNeuroid</code>
     */
    public void addAxon(Neuroid srcNeuroid, Vector synapses) {
	// Raise an exception if neuroid is not found in area.
	if (!neuroids.contains(srcNeuroid)) 
	    throw new RuntimeException("Neuroid " + srcNeuroid + " not found in Area.");
	
	// Add to existing Vector in hash if connections already exist 
	Vector existingSynapses = (Vector) axons.get(srcNeuroid);
	if (existingSynapses == null)
	    axons.put(srcNeuroid, synapses); // Create new
	else 
	    existingSynapses.addAll(synapses); // Add to existing
    }

    /**
     * Creates new <code>numberOfSynapses</code> <code>Synapse</code>s for a given 
     * <code>srcNeuroid</code> to a specified destination <code>destArea</code>.
     * AxonArbor makes sure to return a set of synapses to distinct neurons (no repetitions!)
     * <p>TODO: maybe put this method back into <code>Area</code>?
     * <code>createRandomSynapse</code> should automatically add the synapse?
     * @param numberOfSynapses an <code>int</code> value
     * @return a <code>Vector</code> value
     */
    public AxonArbor createRandomSynapses(Synapse destSynapseTemplate, Neuroid srcNeuroid,
					  int numberOfSynapses) {
	AxonArbor axon =
	    new AxonArbor(destSynapseTemplate, srcNeuroid, this,
			  numberOfSynapses);

	for (int index = 0; index < numberOfSynapses; index++) {
	    int retries = 10, // Retries for coincides with previously allocated synapses
		retry = retries; 

	    while (retry-- > 0) {
		try {
		    axon.addSynapse(axon.createRandomSynapse());
		    break;	// Success
		} catch (ResynapseException e) {
		    // nothing
		    System.out.println("CLASH! Searching for a new neuron to synapse!");
		}
		 
	    } // end of while (retry-- > 0)
	    if (retry <= 0) 
		throw new Error("ERROR: Could not find neuron to synapse in " + retries +
				"tries."); 
	    
	} // end of for (int index = 0; index < numberOfSynapses; index++)

	return axon;
    }

    /**
     * Hack to arbitrarily choose different neurons for each allocation of concepts.
     * A variable <code>conceptCount</code> is used to interleave the allocated neurons. 
     * Return a <code>Vector</code> of new <code>numberOfSynapses</code> <code>Synapse</code>s.
     * <code>AxonArbor</code> makes sure to return a set of synapses to distinct neurons
     * (no repetitions!)
     * @see #conceptCount
     * @param numberOfSynapses an <code>int</code> value
     * @return a <code>Vector</code> value
     */
    public AxonArbor createArbitrarySynapses(Synapse destSynapseTemplate, Neuroid srcNeuroid,
					     int numberOfSynapses) {
	AxonArbor axon =
	    new AxonArbor(destSynapseTemplate, srcNeuroid, this,
			  numberOfSynapses);

	// Calculate possible number of concepts that'll fit into this area.
	int maxConcepts = numberOfNeuroids / numberOfSynapses;

	for (int index = 0; index < numberOfSynapses; index++) {
	    axon.add(axon.createSynapse((Neuroid) neuroids.elementAt(conceptCount +
								     index*maxConcepts)));
	} // end of for (int index = 0; index < numberOfSynapses; index++)
	conceptCount++;		// Increment counter for interleaving next concept
	return axon;
    }

    /**
     * Returns a <code>Neuroid</code> which is a random member of the <code>destArea</code>.
     * <p>TODO: one might might pseudo-random (organized) behavior in selecting destinations
     * (like createArbitrarySynapses).
     * <p>TODO: If a allocate-on-demand approach is used for neuroids,
     * in case of finding a non-existing neuroid should result in its creation.
     * @see #createArbitrarySynapses
     * @see #destArea
     * @return a <code>Neuroid</code> value
     */
    public Neuroid getRandomNeuroid() {
	return (Neuroid) neuroids.elementAt((int)(Math.random()*numberOfNeuroids));
    }

    /**
     * Public method to receive spikes from other areas.
     * TO DO: get an AxonArbor and process synapses in the local area.
     * OBSOLETE: redundant, instead use <code>Synapse.receiveSpike()</code> directly.
     * @see Synapse
     * @param synapse Synapse to spike.
     */
    public void receiveSpike(Synapse synapse) {
	synapse.receiveSpike();
    }
    
    /**
     * Just increments the time by <code>deltaT</code>.
     * Initial idea was to read current time from hardware clock. 
     * TODO: extend this class for the parallel version. (should we?)
     */
    void updateTime() {
	// DO IT
	time += deltaT; 
    }

    /**
     * Updates the state of the <code>Area</code>.
     * Updates all <code>Neuroid</code>s contained within. 
     * This method returns immediately, the thread does the calculation afterwards.
     * TODO: Should be called from a thread with certain frequency. (should it?)
     * @see Neuroid
     * @see Area#run
     */
    public void step() {
/*	updateTime();
	Iteration.loop(neuroids.iterator(), new Utils.Task() {
		public void job(Object o) {
		    ((Neuroid)o).step();
		}
	    });*/

	//System.out.println("In step of " + this);

	// wait until released
	while (stepRequested) Thread.yield();
	//synchronized (isCalculating) { // Wait if thread still doing the calculation
	if (stepRequested) 
	    throw new RuntimeException("Mutual exclusion failed!");
	
	stepRequested = true;	// Notify waiting thread of request
	thread.interrupt();
	//}

    }

    /**
     * Fires neuroid. Looks up synapses that its axon targets and calls <code>receiveSpike</code>
     * method of all <code>Synapse</code>s.
     *
     * @see Neuroid
     * @see Synapse#receiveSpike
     * @param neuroid <code>Neuroid</code> to fire
     */
    void fireNeuroid(Neuroid neuroid) {
	Vector synapses = (Vector)axons.get(neuroid);
	if (synapses == null) return;
	Iteration.loop(synapses.iterator(), new Utils.Task() {
		public void job(Object o) {
		    if (o instanceof Synapse) 
			((Synapse)o).receiveSpike();
		    else {
			try {
			((Remote.SynapseInt)o).receiveSpike();
			} catch (java.rmi.RemoteException e) {
			    System.out.println("Cannot call Remote.Synapse");
			}
		    } // end of else
		    //System.out.println("***synapse");
		}});
    }

    /**
     * Method inherited from java.lang.Object to display text about <code>Area</code>.
     *
     * @return a <code>String</code> value
     */
    public String toString() {
	return "Area: " + name + " at t=" + Network.numberFormat.format(time);
    }

    /**
     * Describe Area in more detail.
     *
     * @return a <code>String</code> value
     */
    public String getStatus() {
	return this + ", numberOfNeuroids=" + numberOfNeuroids +
	    ", replication=" + replication + ", deltaT=" + deltaT + ", tau_m=" + timeConstantM;
    }

    // implementation of java.lang.Runnable interface
    /**
     * Check if step()ing requested, and serve the rquest
     * @see Area#step
     */
    public synchronized void run() {
	while (true) {		// Wasting CPU time!!!

	    try {
		wait();	// Wait to be awakened
	    } catch (InterruptedException e) {
		//throw new RuntimeException("interrupt!");
		updateTime();
		while (true) {
		    try {
			Iteration.loop(neuroids.iterator(), new Utils.Task() {
				public void job(Object o) {
				    ((Neuroid)o).step();
				}
			    });
			break;		// out of while
		    } catch (ConcurrentModificationException ez) {
			// do nothing, i.e. restart
			System.out.println("Concurrent modification in Area.step(), repeating...");
		    }	     
		} // end of while (true)
		stepRequested = false; // Completed
	    }
	    
/*	    if (stepRequested) {
		synchronized (isCalculating) {

		    //System.out.println("request acknowledged");

		    stepRequested = false; // Completed
		} // end of synchronized
	    } else Thread.yield();*/
	    
	} // end of while (true)
    }

}
