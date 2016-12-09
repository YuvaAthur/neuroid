package neuroidnet.ntr;

import neuroidnet.ntr.*;
import neuroidnet.periphery.*;
import neuroidnet.remote.*;
import edu.ull.cgunay.utils.*;

import java.util.*;
import java.io.*;
import java.text.*;
import java.lang.*;
//import java.rmi.*;

// * $Id$
/**
 * <p>Container for all <code>Area</code>s that hold <code>Neuroid</code>s.
 * Classes extending this one should describe the network architecture and topology.
 *
 * <p>Various utility functions to be used from the BeanShell environment are
 * defined here. Implements <code>Serializable</code> so that instances of this class
 * (representing the run-time object of the network) can be stored and retrieved as
 * snapshots.
 *
 * @see Area
 * @see Neuroid
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version $Revision$
 * @since 1.0
 */
abstract public class Network
    implements DebuggerInterface, Serializable, Expressive, Simulation {
    /**
     * All <code>Area</code>s contained in <code>Network</code>.
     * @see Area
     */
    final protected Vector areas = new Vector();

    /**
     * Peripheral control device
     */
    protected Peripheral peripheral;

    /**
     * Redundant pointer to instance of the ConceptArea (it is also in <code>areas</code>)
     * @see #areas
     * @see ConceptArea
     */
    protected ConceptArea conceptArea;
    
    /**
     * Get the value of conceptArea.
     * @return value of conceptArea.
     */
    public ConceptArea getConceptArea() {
	return conceptArea;
    }
    
    /**
     * Set the value of conceptArea.
     * @param v  Value to assign to conceptArea.
     */
    public void setConceptArea(ConceptArea  v) {
	this.conceptArea = v;
    }
    

    /**
     * Increment of time for algorithms.
     * TODO: Put it elsewhere; too far to reach!
     */
    public double deltaT;
    
    /**
     * For formatting real values
     */
    public static NumberFormat numberFormat;


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
     * That is, <code>Task</code> object that will run <code>Area.step()</code>
     * for all areas contained.
     * @see AreaInt
     */
    Task stepArea = new Task() {
		public void job(Object o) {
		    try {
			((AreaInt)o).step(); // Done in parallel!!!
		    } catch (java.rmi.RemoteException e) { 
			e.printStackTrace();
			throw new RuntimeException("Cannot call remote Area.step()");
		    }
		}
	    };

    /**
     * Lock variable and thread controller for event dispatching to remote (or local) areas.
     */
    volatile Simulation simulateAreas;

    /**
     * Flag to mean multi thread usage.
     */
    boolean isConcurrent;

    /**
     * Adds an area to the network.
     *
     * @param area an <code>Area</code> value
     */
    public void addArea(Area area) {
	areas.add(area);
    }

    Set watchList = new HashSet();
    
    /**
     * Get the value of watchList.
     * @return value of watchList.
     */
    public Set getWatchList() {
	return watchList;
    }
    
    /**
     * Set the value of watchList.
     * @param v  Value to assign to watchList.
     */
    public void setWatchList(Set  v) {
	this.watchList = v;
    }

    /**
     * Returns the <code>Area</code> object given the name. 
     * Access method for observing network state.
     *
     * @param name a <code>String</code> value
     * @return an <code>Area</code> value
     */
    public Area getArea(final String name) throws NameNotFoundException {

	try {
	    Iteration.loop(areas, new Task() {
		public void job(Object o) throws TaskException {
		    if (((Area)o).getName().equals(name)) 
			throw new BreakOutOfIterationException(o);
		}
	    });	     
	} catch (BreakOutOfIterationException e) {
	    return (Area)e.getValue();
	} // end of try-catch
	
	throw new NameNotFoundException("Error: Cannot find Area with name '" + name + "'");
    }


    /**
     * Returns the <code>Neuroid</code> object, given the area and id. 
     * Access method for observing network state.
     *
     * @param area <code>Area</code> in which the neuroid resides.
     * @param neuroidId The id of neuroid in given area.
     * @return a <code>Neuroid</code> value
     * @see Neuroid#id
     * @see Area
     * @see #getNeuroid(String,int)
     */
    public Neuroid getNeuroid(Area area, int neuroidId) {
	return (Neuroid) area.neuroids.elementAt(neuroidId);
    }

    /**
     * Returns the <code>Neuroid</code> object, given the area and id. 
     * Access method for observing network state.
     *
     * @param areaName Name of the <code>Area</code> in which the neuroid resides.
     * @param neuroidId The id of neuroid in given area.
     * @return a <code>Neuroid</code> value
     * @see Neuroid#id
     * @see Area
     * @see #getNeuroid(Area,int)
     */
    public Neuroid getNeuroid(String areaName, int neuroidId) throws NameNotFoundException {
	return getNeuroid(getArea(areaName), neuroidId);
    }

    public Neuroid getNeuroid(NeuroidName neuroidName) throws NameNotFoundException {
	return getNeuroid(neuroidName.getAreaName(), neuroidName.getNeuroidId());
    }

    /**
     * Sets the watch flag of the neuroid and includes in the list of watched entities.
     *
     * @param neuroid a <code>Neuroid</code> value
     */
    public void addWatch(Neuroid neuroid) {
	neuroid.setWatch(true);
	watchList.add(neuroid);
    }


    /**
     * <code>addWatch</code>s all neuroids with names given in argument.
     *
     * @param neuroidNames a <code>Set</code> value
     * @see #addWatch(Neuroid)
     */
    public void addWatchAll(Set neuroidNames) throws NameNotFoundException {
	try {
	    new Iteration() {
		public void job(Object o) throws TaskException {
		    try {
			addWatch(getNeuroid((NeuroidName)o));		     
		    } catch (NameNotFoundException e) {
			throw new BreakOutOfIterationException(o);
		    } // end of try-catch
		}
	    }.loop(neuroidNames);
	} catch (BreakOutOfIterationException e) {
	    throw new NameNotFoundException("" + e.getValue());
	} // end of try-catch
    }

    /**
     * Does nothing. TODO: should do something.
     */
    public void init() {}

    /**
     * Updates the state of the <code>Network</code>.
     * Updates all <code>Area</code>s contained within. 
     * TODO: Should be called from a thread with certain frequency.
     * TODO: parallel version should not have this.
     * @see Area
     */
    public void step() {
	//	if (isConcurrent) 
	simulateAreas.step();
/*	else 
	    UninterruptedIteration.loop(areas, stepArea);*/
    }

    /**
     * Delegates the termination request to areas.
     *
     */
    public void stop() {
	simulateAreas.stop();
    }

    /**
     * Describes the architecture of the network.
     * Should be defined in subclasses.
     */
    protected abstract void build();

    /**
     * Runs the simulation on the network.
     * Should be defined in subclasses.
     * @see #advanceTime
     * @deprecated See advanceTime
     */
    protected/* abstract*/ void simulation() /*;*/ {}


    /**
     * Return brief info about the <code>network</code>.
     *
     * @return a <code>String</code> value
     */ 
    public String toString() {
	return getClass().getName();
    }

    /**
     * Adds time from <code>conceptArea</code>.
     *
     * @return a <code>String</code> value
     */
    public String getStatus() {
	return this + " at time=" + conceptArea.time;
    }

    /**
     * Return detailed info about network components.
     *
     * @return a <code>String</code> value
     */
    public String getProperties() {
	return
	    getStatus() + " {\n" +
	    "Peripheral: {\n" + peripheral.getProperties() + "\n},\n" +
	    new StringTask() {
		public void job(Object o) {
		    super.job(((Area)o).getProperties() + "\n");
		}
	    }.getString(areas) + "}\n";
    }

    /**
     * Simulate network for given duration starting from current state.
     *
     * @param msecs a <code>double</code> value to simulate this network
     */
    public void advanceTime(double msecs) {
	if (peripheral == null) 
	    throw new Error("Fatal: no peripheral set in " + this + ".");
	
	double untilTime = 30.0;
	long startTime = System.currentTimeMillis();
	int steps = (int) (msecs / deltaT);
	for (int i = 0; i < steps; i++) {
	    //System.out.println("STEP " + i);

	    peripheral.step();		// step deltaT and initiates peripheral actions
	}
	double elapsed = System.currentTimeMillis() - startTime;
	System.out.println("Elapsed time: " + elapsed + " msecs (1 simulation msec = " +
			   elapsed/msecs + " msecs).");
    }

    /**
     * Creates a new <code>Network</code> instance.
     *
     * @param deltaT a <code>double</code> value, time increments for each simulation step.
     * @param isConcurrent a <code>boolean</code> value, indicating if the network
     * should be simulated in distributed fashion.
     * @see Network#build
     * @see Network#simulation
     */
    public Network(double deltaT, boolean isConcurrent) {
	this.deltaT = deltaT;
	this.isConcurrent = isConcurrent;

	setNumberFormatting();

	conceptArea = new ConceptArea(this);
	areas.add(conceptArea);


	//stepArea;

	// The following are removed for the debug shell version, should be called explicitly 
	// from any extending class.

	/* run();
	   finale();*/
    }

    /**
     * Number formatting for text numberFormat
     * @see #numberFormat
     * @see #Network
     * @see #readObject
     */
    void setNumberFormatting() {

	numberFormat = NumberFormat.getInstance(); // Get NumberFormat instance
	try {
	    // Change the infinity symbol from '?' 
	    DecimalFormatSymbols symbols = ((DecimalFormat)numberFormat).getDecimalFormatSymbols();
	    symbols.setInfinity("Inf");
	    ((DecimalFormat)numberFormat).setDecimalFormatSymbols(symbols);
	} catch (Throwable e) {
	    System.out.println("Warning: " + e + "\n"
			       + "Unable to format numbers in current locale...");
	} finally {
	    numberFormat.setMaximumFractionDigits(3);
	} // end of try-catch
    }

    /**
     * Build the network (USED TO: and run the simulation.)  Calls
     * <code>build()</code>. Initializes network for parallel or
     * single-threaded execution.
     * <p>TODO: Change this to build?
     *
     * @see #build
     * @see #simulation
     */
    public void run () {
	if (isConcurrent) 
	    simulateAreas = new ParallelTask(areas, stepArea);
	else 		// Sequential version, single thread
	    simulateAreas = new Simulation() {
		    public void init() { }

		    public void step() {
			UninterruptedIteration.loop(areas, stepArea);
		    } 

		    public void stop() {
			new UninterruptedIteration() {
			    public void job(Object o) {
				((Simulation) o).stop();
			    }
			}.loop(areas);
		    }
		};
	
	build();		// creates areas and connections
	simulateAreas.init();	// create & start threads for concurrent version
    }

    /**
     * To be called after everthing else is done. Terminates all
     * threads, theoretically should terminate.
     */
    public void finale () {
	    //System.out.println("Network status: \n" + this.getProperties());
	    simulateAreas.stop();
    }

    /**
     * Method called when a serialized object is loaded.
     * Only customization done is to set the <code>static numberFormat</code>
     * object via a call to <code>setNumberFormatting()</code>.
     *
     * @param in a <code>java.io.ObjectInputStream</code> value
     * @exception IOException if an error occurs
     * @exception ClassNotFoundException if an error occurs
     */
    private void readObject(java.io.ObjectInputStream in)
	throws IOException, ClassNotFoundException {
	setNumberFormatting();	// We need to set the static variable numberFormat
	in.defaultReadObject();	// Real method that does reading
    }

}
