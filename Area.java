import java.lang.*;
import java.util.*;
import java.rmi.*;
import Utils.*;

/**
 * Entity <code>Area</code> that holds neurons and keeps track of time.
 *
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version 1.0
 * @since 1.0
 */

public class Area implements Runnable {

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
     * TODO: Put it elsewhere; too far to reach!
     */
    double deltaT;

    /**
     * Id tag of the area.
     */
    public int id; // identification tag

    /**
     * Number of <code>Neuroid</code>s contained in <code>Area</code>.
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
     * @see Neuroid
     */
    Vector neuroids;

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
    Hashtable axons;

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

    /**
     * Creates a new <code>Area</code> instance with <code>numberOfNeuroids</code> of
     * <code>Neuroids</code>.
     * <p>TODO: If a allocate-on-demand approach is used for Neuroids none
     * should be allocated at this time.
     * @see Neuroid
     *
     * @param numberOfNeuroids 
     * @param the replication factor 
     */
    public Area(String name, int numberOfNeuroids,
		int replication, double deltaT, double period, double threshold) {
	this.numberOfNeuroids = numberOfNeuroids;
	this.replication = replication;
	this.deltaT = deltaT;
	this.period = period;
	this.name = name;

	// Outgoing connections of neuroids held here
	axons = new Hashtable();

	// Neuroids in Area
	neuroids = new Vector(); 

	// Add inhibitory inter-neuron: one neuron that takes input from all neuroids and
	// projects to all. Threshold is fixed to fire above 2*replication inputs
	// weights and threshold should *not* be modified? refraction?
	inhibitoryInterNeuroid = new Neuroid(this, numberOfNeuroids, /*2**/replication*0.9, 1);
	neuroids.add(inhibitoryInterNeuroid);
	Vector inhibitorySynapseVector = new Vector();

	// Instantiate Neuroids
	for (int i = 0; i < numberOfNeuroids; i++) { // Enumerate neuroids
	    Neuroid neuroid = new Neuroid(this, i, threshold, 1);
	    neuroids.add(neuroid); // refractoryTimeConstant=1

	    // Add inhibitory synapse to neuroid from inhibitoryInterNeuroid
	    inhibitorySynapseVector.add(new Synapse(neuroid, 1, deltaT, true)); 

	    // Add an initial excitory synapse to inhibitoryInterNeuroid from neuroid
	    Vector oneSynapse = new Vector();
	    oneSynapse.add(new Synapse(inhibitoryInterNeuroid, 1, deltaT, false)); 
	    axons.put(neuroid, oneSynapse);
	}

	axons.put(inhibitoryInterNeuroid, inhibitorySynapseVector);

	// Create a thread to do the step()s
	thread = new Thread(this);
	thread.start();
    }

    /**
     * Makes connections between this <code>Area</code> and the given <code>destArea</code>.
     * Uses modified connection prbability of rrandom multipartite graphs to determine number
     * of <code>Neuroid</code>s to be connected on destination <code>Area</code>.
     *
     * @see Neuroid
     * @param destArea the <code>Area</code> to which this one is connected.
     */
    public void connectToArea(Object destArea) {
	// TODO: don't connect inhibitoryInterNeuroid!
	int destReplication, destNumberOfNeuroids;

	if (destArea instanceof Area) {
	    destReplication = ((Area)destArea).getReplication();
	    destNumberOfNeuroids = ((Area)destArea).getNumberOfNeuroids();
	} else {
	    try {
		destReplication = ((RemoteAreaInt)destArea).getReplication();
		destNumberOfNeuroids = ((RemoteAreaInt)destArea).getNumberOfNeuroids();	     
	    } catch (RemoteException e) {
		System.out.println("Cannot call RemoteArea methods.");
		return;
	    }
	} // end of else	
	
	// Valiant's connection probability for random multipartite graphs 
	double connProb = Math.sqrt((double)destReplication /
				    (destNumberOfNeuroids * replication * replication ));
	// Number of neuroids in the destination to be connected to each neuroid here
	int numberOfConnections = (int) (destNumberOfNeuroids * connProb);

	System.out.println("Conn prob from " + this + " to " + destArea + " is " +
			   connProb + "(" + numberOfConnections + " Neuroids)");

	// TODO: change to make use of Iteration.loop()
	Object[] p = { destArea, new Integer(numberOfConnections)};
	Iteration.loop(neuroids.iterator(), new Utils.TaskWithParam() { 
		public void job(Object o, Object[] p) {
		    Neuroid fromNeuroid = (Neuroid) o;
		    Object _destArea = p[0];
		    int _numberOfConnections = ((Integer) p[1]).intValue();
		    try {
	    
			Vector synapses = 
			    (_destArea instanceof Area) ?
			    ((Area)_destArea).createRandomSynapses(_numberOfConnections):
			    ((RemoteAreaInt)_destArea).createRandomSynapses(_numberOfConnections); 

			// Add to existing Vector in hash if connections already exist 
			Vector existingSynapses = (Vector) axons.get(fromNeuroid);
			if (existingSynapses == null)
			    axons.put(fromNeuroid, synapses); // Create new
			else 
			    existingSynapses.addAll(synapses); // Add to existing
			
		    } catch (RemoteException e) {
			System.out.println("Cannot call RemoteArea methods.");
		    }

		    //System.out.println("Create " + synapses.size() + " synapses leaving " +
		    //fromNeuroid);
		    /*
		      Iteration.loop(synapses.iterator(), new Utils.Task() { 
		      public void job(Object o) {
		      System.out.println("New " + ((o instanceof Synapse) ?
		      (""+(Synapse)o) : (""+(RemoteSynapseInt)o)));
		      }});*/
		}}, p);
    }

    /**
     * Return a <code>Vector</code> of new <code>numberOfSynapses</code> <code>Synapse</code>s.
     * AxonArbor makes sure to return a set of synapses to distinct neurons (no repetitions!)
     * @param numberOfSynapses an <code>int</code> value
     * @return a <code>Vector</code> value
     */
    public Vector createRandomSynapses(int numberOfSynapses) {
	AxonArbor axon = new AxonArbor(numberOfSynapses);
	for (int index = 0; index < numberOfSynapses; index++) {
	    int retry = 10; // Retries for coincides with previously allocated synapses
	    while (retry-- > 0) {
		try {
		    axon.addSynapse(createRandomSynapse());
		    retry = 0;	// Success
		} catch (ResynapseException e) {
		    // nothing
		    System.out.println("CLASH! Searching for a new neuron to synapse!");
		}
		 
	    } // end of while (retry-- > 0)
	    
	} // end of for (int index = 0; index < numberOfSynapses; index++)
	return axon;
    }

    /**
     * Hack to arbitrarily choose different neurons for each allocation of concepts.
     * A variable <code>conceptCount</code> is used to interleave the allocated neurons. 
     * Return a <code>Vector</code> of new <code>numberOfSynapses</code> <code>Synapse</code>s.
     * AxonArbor makes sure to return a set of synapses to distinct neurons (no repetitions!)
     * @see #conceptCount
     * @param numberOfSynapses an <code>int</code> value
     * @return a <code>Vector</code> value
     */
    public Vector createArbitrarySynapses(int numberOfSynapses) {
	Vector axon = new Vector(numberOfSynapses);

	// Calculate possible number of concepts that'll fit into this area.
	int maxConcepts = numberOfNeuroids / numberOfSynapses;

	for (int index = 0; index < numberOfSynapses; index++) {
	    axon.add(createSynapse((Neuroid) neuroids.elementAt(conceptCount +
								index*maxConcepts)));
	} // end of for (int index = 0; index < numberOfSynapses; index++)
	conceptCount++;		// Increment counter for interleaving next concept
	return axon;
    }

    /**
     * Creates a new synapse connected to a random member of the <code>Area</code>.
     * SRM parameters: timeConstantM = 1, timeConstantS = deltaT, excitory 
     * @see deltaT
     * @return a <code>Synapse</code> value */
    public Synapse createRandomSynapse() { return createSynapse(getRandomNeuroid()); }

    /**
     * Create a synapse with predefined characteristics.
     * TODO: Make an inner class to specify these characteristics and
     * put these functions in there.
     * @param neuroid a <code>Neuroid</code> value
     * @return a <code>Synapse</code> value
     */
    public Synapse createSynapse(Neuroid neuroid) {
	return new Synapse(neuroid, 1, deltaT, false);
    }

    /**
     * Returns a <code>Neuroid</code> which is a random member of the <code>Area</code>.
     * <p>TODO: one might might pseudo-random (organized) behavior in selecting destinations.
     * <p>TODO: If a allocate-on-demand approach is used for neuroids,
     * in case of finding a non-existing neuroid should result in its creation.
     *
     * @return a <code>Neuroid</code> value
     */
    public Neuroid getRandomNeuroid() {
	return (Neuroid) neuroids.elementAt((int)(Math.random()*numberOfNeuroids));
    }

    /**
     * Public method to receive spikes from other areas.
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
	if (stepRequested) {
	    throw new RuntimeException("Mutual exclusion failed!");
	}
	stepRequested = true;
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
			((RemoteSynapseInt)o).receiveSpike();
			} catch (RemoteException e) {
			    System.out.println("Cannot call RemoteSynapse");
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
	return "Area: " + name + " at t=" + time;
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
		Iteration.loop(neuroids.iterator(), new Utils.Task() {
			public void job(Object o) {
			    ((Neuroid)o).step();
			}
		    });
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
