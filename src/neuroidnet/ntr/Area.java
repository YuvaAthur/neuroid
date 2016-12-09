package neuroidnet.ntr;

import neuroidnet.periphery.*;
import neuroidnet.remote.*;
import edu.ull.cgunay.utils.*;

import java.lang.*;
import java.lang.reflect.*;
import java.util.*;
import java.text.*;
import java.io.*;

/**
 * Entity <code>Area</code> that holds neurons and keeps track of time.
 *
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version 1.0
 * @since 1.0
 */

public class Area
    implements Runnable, AreaInt, Serializable, Expressive, Simulation {

    /**
     * Name of the <code>Area</code> for identification purposes.
     */
    String name;

    /**
       * Get the value of name.
       * @return value of name.
       */
    public String getName() {return name;}
    
    /**
       * Set the value of name.
       * @param v  Value to assign to name.
       */
    public void setName(String  v) {this.name = v;}

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
    protected int numberOfNeuroids;
    
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
     * The global inhibitory neuroid that gets input from all and projects to all in the area.
     */
    Neuroid inhibitoryInterNeuroid;

    /**
     * The template used for creating synapses from the <code>inhibitoryInterNeuroid</code>
     * @see #inhibitoryInterNeuroid
     */
    Synapse fromInhibitorySynapseTemplate;

    /**
     * The template used for creating synapses to the <code>inhibitoryInterNeuroid</code>
     * @see #inhibitoryInterNeuroid
     */
    Synapse toInhibitorySynapseTemplate;

    /**
     * The axon emanating from the inhibitoryInterNeuroid
     * @see #init
     */
    AxonArbor inhibitorySynapseVector;

    /**
     * Hash table holding the white matter, e.g. the axons of neuroids residing in this
     * <code>Area</code> connecting to <code>Synapse</code>s of other <code>Neuroid</code>s.
     * Returns a <code>Vector</code> of <code>Synapses</code> for every <code>Neuroid</code> key.
     * @see Synapse
     * @see Neuroid
     */
    protected Hashtable axons = new Hashtable();
    
    /**
     * Get the value of axons.
     * @return value of axons.
     */
    public Hashtable getAxons() {
	return axons;
    }
    
    /**
     * Set the value of axons.
     * @param v  Value to assign to axons.
     */
    public void setAxons(Hashtable  v) {
	this.axons = v;
    }
    
    /**
     * Lock variable showing the thread is busy doing the calculations.
     * @see #stepRequested
     * @see Area#run
     * @see Area#step
     */
    transient volatile Object isCalculating = new Object();

    /**
     * Lock variable showing the thread is waiting for a step request.
     * @see #stepRequested
     * @see Area#run
     * @see Area#step
     */
    transient volatile Object isWaiting = new Object();

    /**
     * Flag showing incoming requets to step().
     * @see Area#run
     * @see Area#step
     */
    volatile boolean stepRequested = false;

    /**
     * Thread to do the actual calculations. Is not part of the persistent object.
     */
    transient Thread thread;

    /**
       * Get the value of thread.
       * @return value of thread.
       */
    public Thread getThread() {return thread;}
    
    /**
       * Set the value of thread.
       * @param v  Value to assign to thread.
       */
    public void setThread(Thread  v) {this.thread = v;}

    /**
     * Count of concept allocations made (via call to addArbitrarySynapses)
     * in this <code>Area</code>.
     * @see #addArbitrarySynapses
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
     * True if the area contains a inhibitory inter-neuroid.
     */
    boolean inhibInter;

    /**
     * Membrane time constant of all neurons in this area.
     */
    public double timeConstantM;

    /**
     * The threshold to change a Neuroid from AM to AM1 mode.
     * @see Neuroid#step
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
     * Constructor for plain Area (no inhibitory interneuron). Calls other constructor.
     * @see #Area(Network,String,int,int,double,double,boolean,double,double)
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
		double refractoryTimeConstant, Class neuroidClass) {
	this(network, name, numberOfNeuroids, replication, period, threshold, false,
	     timeConstantM, refractoryTimeConstant, neuroidClass);
    }

    /**
     * Constructor with option to add inhibitory inter-neuron. 
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
    public Area(Network network, String name, int numberOfNeuroids, int replication,
		double period, double threshold, boolean inhibInter, double timeConstantM,
		double refractoryTimeConstant, Class neuroidClass) {
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

	// The activation threshold that makes neuroids go from AM to AM1
	// @see Neuroid.step
	activationThreshold = threshold;

	Constructor cons;
	if (numberOfNeuroids > 0 || inhibInter) {
	    try {
		Class[] consParams = { Area.class, double.class, double.class };
		cons = neuroidClass.getConstructor(consParams);
	    } catch (NoSuchMethodException e) {
		throw new Error("Fatal: Constructor not found in the given Neuroid class " +
				neuroidClass);
	    } 


	    if (inhibInter) {
		// Add inhibitory inter-neuron: one neuron that takes input from all neuroids and
		// projects to all. Threshold is fixed to fire above ~replication inputs
		// weights and threshold should *not* be modified? refraction?
		Object[] params = {this, new Double(replication*0.9),
				   new Double(refractoryTimeConstant)};
		try {
		    inhibitoryInterNeuroid = (Neuroid)cons.newInstance(params);
		} catch (Exception e) {
		    throw new Error("Fatal: Cannot instantiate Neuroid " + neuroidClass);
		} // end of catch
		//new PeakerNeuroid(this, replication*0.9, refractoryTimeConstant);
		fromInhibitorySynapseTemplate =
		    new Synapse(null, null, 0.5, deltaT, true, 0); // no delay
		toInhibitorySynapseTemplate =
		    new Synapse(null, null, 1, deltaT, false, 0); // no delay
		inhibitorySynapseVector =
		    new AxonArbor(fromInhibitorySynapseTemplate, inhibitoryInterNeuroid, this); 

		try {
		    // Create concept
		    (new SensoryConcept(network, "Area: " + name + " inhibitory neuroid")).
			attach(inhibitoryInterNeuroid);
		} catch (ConceptSaturatedException e) {
		    e.fillInStackTrace();
		    throw new RuntimeException("Fatal: Cannot attach to inhibitory inter neuroid concept.");
		} // end of try-catch
    
	    } // end of if (inhibInter)

	    // Instantiate Neuroids
	    try {
		Object[] params = {this, new Double(activationThreshold),
				   new Double(refractoryTimeConstant)};

		for (int i = 0; i < numberOfNeuroids; i++)  // Enumerate neuroids
		    cons.newInstance(params);
	    } catch (Exception e) {
		throw new Error("Fatal: Cannot instantiate Neuroid " + neuroidClass);
	    } // end of catch

	} // end of if (numberOfNeuroids > 0 || inhibInter)

	// Create a thread to do the step()s
	thread = new Thread(this);
	thread.start();
    }

    /**
     * Adds a neuroid to the <code>Area</code>. Determines a sequence number according to
     * the <code>neuroid</code>'s order in the <code>Vector neuroids</code>.
     * if specified in the constructor, a simple lateral circuit is formed. A link from this
     * neuroid to the globally inhibitory neuroid is made. 
     * @param neuroid a <code>Neuroid</code> value to add into this <code>Area</code>
     * @return the id of the neuroid (i.e. index in <code>neuroids</code>)
     * @see Neuroid#id
     */
    public int addNeuroid(Neuroid neuroid) {

	// Thread safe
	synchronized (neuroids) {
	    neuroids.add(neuroid);
	}

	Vector axon = new Vector();

	try {
	    if (inhibInter) {
		// Add a predefined synapse from inhibitoryInterNeuroid to neuroid
		inhibitorySynapseVector.addNeuroid(neuroid); 

		// Add an initial excitory synapse to inhibitoryInterNeuroid from neuroid
		AxonArbor oneSynapse =
		    new AxonArbor(toInhibitorySynapseTemplate, neuroid, this);
		oneSynapse.addNeuroid(inhibitoryInterNeuroid);

		axon.add(oneSynapse);
	    } // end of if (inhibInter)	     
	} catch (ResynapseException e) {
	    throw new Error("Fatal: " + e);
	} // end of try-catch

	axons.put(neuroid, axon);

	return neuroids.indexOf(neuroid);
    }

    /**
     * Makes connections between this <code>Area</code> and the given <code>destArea</code>.
     * Uses modified connection probability of random multipartite graphs to determine number
     * of <code>Neuroid</code>s to be connected on destination <code>Area</code>.
     *
     * @see Neuroid
     * @see Synapse
     * @param destArea the <code>Area</code> to which this one is connected.
     * @param timeConstantS time constant to be used in creating synapses
     * @param delay synaptic delay
     * @param nuBoost a <code>double</code> factor to multiply the original probability
     * ratio as to magnify
     */
    public void connectToArea(final AreaInt destArea, double timeConstantS,
			      double delay, double nuBoost) {
	// TODO: don't connect inhibitoryInterNeuroid!
	int destReplication, destNumberOfNeuroids;

	try {
	    destReplication = ((AreaInt)destArea).getReplication();
	    destNumberOfNeuroids = ((AreaInt)destArea).getNumberOfNeuroids(); 
	} catch (java.rmi.RemoteException e) {
	    throw new Error("Cannot call AreaInt methods.");
	}
	
	// Valiant's connection probability for random multipartite graphs 
	double connProb = Math.sqrt((double)nuBoost * destReplication /
				    (destNumberOfNeuroids * replication * replication ));

	// Number of neuroids in the destination to be connected to each neuroid here
	final int numberOfConnections = (int) (destNumberOfNeuroids * connProb);

	System.out.println("Conn prob from " + this + " to " + destArea + " is " +
			   Network.numberFormat.format(connProb) + "(" + numberOfConnections +
			   " Neuroids)");

	final Synapse synapseTemplate =
	    new Synapse(null, null, timeConstantM, timeConstantS, false, delay);

	synchronized (neuroids) {
	    // Loop for every neuroid in this area and create connection in destination area
	    new UninterruptedIteration() { 
		public void job(Object o) {
		    Neuroid srcNeuroid = (Neuroid) o;

		    try {
			// Need to be called at the remote area, so that it can generate 
			// a remote reference to an AxonArbor object 
			destArea.addRandomSynapses(synapseTemplate, srcNeuroid,
						   numberOfConnections); 

		    } catch (java.rmi.RemoteException e) {
			throw new Error("Cannot call remote.Area methods.");
		    }
		}}.loop(neuroids);
	}
    }

    /**
     * Adds the synapses to the outgoing synapse record of the <code>Area</code>.
     * If synapses associated with <code>srcNeuroid</code> exist,
     * new synapses are just added to them.
     * TO DO: get a remote reference to an AxonArbor in the remote Area and put it in hash.
     * @param srcNeuroid the presynaptic <code>Neuroid</code> in this <code>Area</code>.
     * @param synapses the <code>Vector</code> to be associated with <code>srcNeuroid</code>
     */
    public void addAxon(Neuroid srcNeuroid, AxonArbor synapses) {
	// Raise an exception if neuroid is not found in area.
	if (!neuroids.contains(srcNeuroid)) 
	    throw new RuntimeException("Neuroid " + srcNeuroid + " not found in Area.");

	// DOing: TODO: Keep separate AxonArbors for each different Area to that we project.
	((Collection)axons.get(srcNeuroid)).add(synapses);
/*
	// Add to existing Vector in hash if connections already exist 
	if (existingSynapses == null)
	    axons.put(srcNeuroid, synapses); // Create new
	else 
	    existingSynapses.addAll(synapses);*/ // Add to existing
    }

    /**
     * Creates new <code>numberOfSynapses</code> <code>Synapse</code>s for a given 
     * <code>srcNeuroid</code> to a specified destination <code>destArea</code>.
     * AxonArbor makes sure to return a set of synapses to distinct neurons (no repetitions!)
     * <p>TODO: maybe put this method back into <code>Area</code>?
     * <code>addRandomSynapse</code> should automatically add the synapse?
     *
     * @param destSynapseTemplate a <code>Synapse</code> value
     * @param srcNeuroid a <code>Neuroid</code> value
     * @param numberOfSynapses an <code>int</code> value
     */
    public void addRandomSynapses(Synapse destSynapseTemplate, Neuroid srcNeuroid,
				     int numberOfSynapses) {
	AxonArbor axon =
	    new AxonArbor(destSynapseTemplate, srcNeuroid, this,
			  numberOfSynapses);

	for (int index = 0; index < numberOfSynapses; index++) {
	    int retries = 20, // Retries for coincides with previously allocated synapses
		retry = retries; 

	    while (retry-- > 0) {
		try {
		    axon.addRandomSynapse();
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
    }

    /**
     * Hack to arbitrarily choose different neurons for each allocation of concepts.
     * A variable <code>conceptCount</code> is used to interleave the allocated neurons. 
     * Return a <code>Vector</code> of new <code>numberOfSynapses</code> <code>Synapse</code>s.
     * <code>AxonArbor</code> makes sure to return a set of synapses to distinct neurons
     * (no repetitions!) Used from SensoryNeuroid.
     * @see #conceptCount
     * @see periphery.SensoryNeuroid#SensoryNeuroid
     *
     * @param destSynapseTemplate a <code>Synapse</code> value
     * @param srcNeuroid a <code>Neuroid</code> value
     * @param numberOfSynapses an <code>int</code> value
     */
    public void addArbitrarySynapses(Synapse destSynapseTemplate, Neuroid srcNeuroid,
					int numberOfSynapses) {
	AxonArbor axon =
	    new AxonArbor(destSynapseTemplate, srcNeuroid, this,
			  numberOfSynapses);

	// Calculate possible number of concepts that'll fit into this area.
	int maxConcepts = numberOfNeuroids / numberOfSynapses;

	try {
	    for (int index = 0; index < numberOfSynapses; index++) 
		axon.addNeuroid((Neuroid) neuroids.elementAt(conceptCount +
							     index*maxConcepts));
	} catch (ResynapseException e) {
	    throw new Error("Fatal: " + e);
	} // end of try-catch
	
	conceptCount++;		// Increment counter for interleaving next concept
	//	return axon;
    }

    /**
     * Returns a <code>Neuroid</code> which is a random member of the <code>destArea</code>.
     * <p>TODO: one might might pseudo-random (organized) behavior in selecting destinations
     * (like addArbitrarySynapses).
     * <p>TODO: If a allocate-on-demand approach is used for neuroids,
     * in case of finding a non-existing neuroid should result in its creation.
     * @see #addArbitrarySynapses
     * @see AxonArbor#destArea
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
     * Does nothing. TODO: should do something.
     */
    public void init() {}

    /**
     * Updates the state of the <code>Area</code>.
     * Updates all <code>Neuroid</code>s contained within. 
     * This method returns immediately, the thread does the calculation afterwards.
     * TODO: Should be called from a thread with certain frequency. (should it?)
     * @see Neuroid
     * @see Area#run
     */
    public synchronized void step() {
	//System.out.println("In step of " + this);
	try {
	    synchronized (isCalculating) { // Wait if thread still doing the calculation
		if (stepRequested) 
		    isCalculating.wait(); 
	    }

	    synchronized (isWaiting) {
		stepRequested = true;	// Notify waiting thread of request
		isWaiting.notify();
	    }
	} catch (InterruptedException e) {
	    throw new Error("unexpected in " + this.getStatus());
	} // end of try-catch

	//throw new RuntimeException("Mutual exclusion failed in " + this.getStatus());
    }

    /**
     * Terminates the thread dedicated to this area.
     * @see #thread
     */
    public void stop() {
	thread.interrupt();
    }

    /**
     * Fires neuroid. Looks up synapses that its axon targets and calls <code>receiveSpike</code>
     * method of all <code>Synapse</code>s.
     * <p> TODO: Make it trigger the Axon to fire.
     * @see Neuroid
     * @see Synapse#receiveSpike
     * @param neuroid <code>Neuroid</code> to fire
     */
    void fireNeuroid(Neuroid neuroid) {
	Vector axon = (Vector)axons.get(neuroid);
	if (axon == null) return;
	UninterruptedIteration.loop(axon.iterator(), new Task() {
		public void job(Object o) {
		    ((Input)o).fire();
		}});
    }

    /**
     * Identifies are with its name.
     *
     * @return a <code>String</code> value
     */
    public String toString() {
	return "Area: " + name;
    }

    /**
     * Mentions the time in addition to <code>toString()</code> contents.
     *
     * @return a <code>String</code> value
     */
    public String getStatus() {
	return this + " at t=" + Network.numberFormat.format(time);
    }

    /**
     * Describe <code>Area</code> in more detail, including static properties.
     *
     * @return a <code>String</code> value
     */
    public String getProperties() {
	return this.getStatus() + ", numberOfNeuroids=" + numberOfNeuroids +
	    ", replication=" + replication + ", deltaT=" + deltaT + ", tau_m=" + timeConstantM;
    }

    // implementation of java.lang.Runnable interface
    /**
     * Check if step()ing requested, and serve the request.
     * @see Area#step
     */
    public void run() {
	try {
	    while (true) {	// Wasting (not too much) CPU time!!!
		
		synchronized (isWaiting) {
		    if (!stepRequested) 
			isWaiting.wait();	// Wait to be awakened
		}

		synchronized (isCalculating) {
		    updateTime();
		    while (true) {
			try {
			    // instead synchronize on neuroids? (may cause deadlock!)
			    UninterruptedIteration.loop(neuroids.iterator(), new Task() {
				    public void job(Object o) {
					((Neuroid)o).step();
				    }
				});
			    break;	// out of while
			} catch (ConcurrentModificationException ez) {
			    // do nothing, i.e. restart
			    System.out.println("Concurrent modification in Area.step(), " +
					       "repeating in " + this);
			}	     
		    } // end of while (true)
		    stepRequested = false; // Completed
		    isCalculating.notify();
		}
	    } // end of while (true)
	} catch (InterruptedException e) {
	    System.out.println("Received interrupt, exiting thread for " + this.getStatus());
	}
    }

    /**
     * Method called when a serialized object is loaded.
     * Only customization done is to create a new thread
     * to reside in the <code>transient thread</code> variable.
     *
     * @param in a <code>java.io.ObjectInputStream</code> value
     * @exception IOException if an error occurs
     * @exception ClassNotFoundException if an error occurs
     */
    private void readObject(java.io.ObjectInputStream in)
	throws IOException, ClassNotFoundException {
	in.defaultReadObject();	// Real method that does reading
	isWaiting = new Object();
	isCalculating = new Object();
	thread = new Thread(this); // Set the transient variable
	thread.start();
    }

}
