package Base;
import Base.*;
import Remote.*;
import java.util.*;
//import java.rmi.*;
import Utils.*;

/**
 * $Id$
 * <p>Container for all <code>Area</code>s that hold <code>Neuroid</code>s.
 * Classes extending this one should describe the network architecture and topology. 
 *
 * @see Area
 * @see Neuroid
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version $Revision$
 * @since 1.0
 */
abstract public class Network {
    /**
     * All <code>Area</code>s contained in <code>Network</code>.
     * @see Area
     */
    protected Vector areas = new Vector();

    /**
     * Peripheral control device
     */
    protected Peripheral peripheral;

    /**
     * Increment of time for algorithms.
     * TODO: Put it elsewhere; too far to reach!
     */
    protected double deltaT;
    
    /**
     * Get the value of peripheral.
     * @return value of peripheral.
     */
    public Peripheral getPeripheral() {return peripheral;}
    
    /**
     * Set the value of peripheral.
     * @param v  Value to assign to peripheral.
     */
    public void setPeripheral(Peripheral  v) {this.peripheral = v;}

    
    /**
     * Defines what do to every step given Area object.
     * @see Area
     */
    Task stepTask;

    /**
     * Lock variable and thread controller for event dispatching to remote (or local) areas.
     */
    volatile ParallelTask pT;

    /**
     * Flag to mean multi thread usage.
     */
    boolean isConcurrent;

    /**
     * Updates the state of the <code>Network</code>.
     * Updates all <code>Area</code>s contained within. 
     * TODO: Should be called from a thread with certain frequency.
     * TODO: parallel version should not have this.
     * @see Area
     */
    public void step() {
	if (isConcurrent) 
	    pStep();
	else 
	    Iteration.loop(areas.iterator(), stepTask);	     
    }

    void pStep() {
	pT.reset();
	try {
	    while (pT.waitcount < areas.size()) {
		//System.out.println("Waiting for others to wait!");
		pT.wait(10);
	    }
	} catch (InterruptedException e) {
	    System.out.println("interrupted!!!" + e);
	    e.printStackTrace();
	}

	pT.notifyAll();
	//System.out.println("notified all!");

	try {
	    //System.out.println("Waiting for others to finish!");
	    //pT.wait();		// Give up lock, become first one in queue!

	    while (pT.runcount > 0) {
		//System.out.println("Waiting for others to finish!");
		pT.wait(10);
	    }
	} catch (InterruptedException e) {
	    System.out.println("interrupted!!!" + e);
	    e.printStackTrace();
	}
    }

    /**
     * Describes the architecture of the network.
     * Should be defined in subclasses.
     */
    protected abstract void build();

    /**
     * Runs the simulation on the network.
     * Should be defined in subclasses.
     */
    protected abstract void simulation();

    /**
     * Sets deltaT and then calls <code>build()</code> and <code>simulation()</code>
     * @see Network#build
     * @see Network#simulation
     * @param deltaT a <code>double</code> value
     */
    public Network(double deltaT, boolean isConcurrent) {
	this.deltaT = deltaT;
	this.isConcurrent = isConcurrent;

	stepTask = new Task() {
		public void job(Object o) {
		    if (o instanceof Area)
			((Area)o).step();
		    else {
			try {
			    ((Remote.AreaInt)o).step(); // Should be done in parallel!!!
			} catch (java.rmi.RemoteException e) { 
			    e.printStackTrace();
			    throw new RuntimeException("Cannot call remote Area.step()");
			}
		    } // end of else
		}
	    };

	if (isConcurrent) {
	    pT = new ParallelTask(areas) {
		    public void init() {
			// iterate over elements and associate threads
			for (Iterator i = this.a.iterator(); i.hasNext(); ) {
			    new Thread(new Objective(this, stepTask, i.next())).start();
			} // end of for (Iterator i = a.iterator(); i.hasNext(); )
		    }
		};

	    build();
	    pT.init();		// create & start threads
	    synchronized (pT) {
		simulation();
	    }
	} else {		// Sequential version, single thread
	    build();
	    simulation();	    
	} // end of if-else (isConcurrent)
	
	System.exit(0);
    }
}
