package neuroidnet.ntr;

import neuroidnet.ntr.*;
import neuroidnet.periphery.*;
import neuroidnet.remote.*;
import neuroidnet.utils.*;

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
abstract public class Network implements DebuggerInterface, Serializable {
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
     * Adds an area to the network.
     *
     * @param area an <code>Area</code> value
     */
    public void addArea(Area area) {
	areas.add(area);
    }

    /** class that searches for an area with matching name */
    public class TaskWPR implements TaskWithReturn {
	Area area = null;
	String name;
	    
	/**
	 * Creates a new <code>TaskWPR</code> instance.
	 *
	 * @param name Area name to be matched.
	 */
	public TaskWPR(String name) { this.name = name; }

	public void job(Object o)  {
	    Area a = (Area) o;
	    if (a.getName().equals(name)) 
		area = a;
	}

	public Object getValue() { return area; }
    }

    Vector watchList = new Vector();
    
    /**
     * Get the value of watchList.
     * @return value of watchList.
     */
    public Vector getWatchList() {
	return watchList;
    }
    
    /**
     * Set the value of watchList.
     * @param v  Value to assign to watchList.
     */
    public void setWatchList(Vector  v) {
	this.watchList = v;
    }

    /**
     * Returns the <code>Area</code> object given the name. 
     * Access method for observing network state.
     *
     * @param name a <code>String</code> value
     * @return an <code>Area</code> value
     */
    public Area getArea(String name) throws NameNotFoundException {

	TaskWPR nameCompareTask = new TaskWPR(name);
	Iteration.loop(areas, nameCompareTask);

	Area retval = (Area) nameCompareTask.getValue();

	if (retval == null) 
	    throw new NameNotFoundException("Error: Cannot find Area with name '" + name + "'");

	return retval;
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
	synchronized (pT) {
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
     * Return detailed info about network components.
     *
     * @return a <code>String</code> value
     */
    public String getStatus() {
	String retval = new String();

	retval += "Peripheral: " + peripheral + "\n";
	//retval += "ConceptArea: \n" + conceptArea.getStatus();

	TaskWithReturn areasToStringTask =
	    new StringTask() {
		public void job(Object o) {
		    this.retval += "" + ((Area)o).getStatus() + "\n";
		}
	    };
	
	Iteration.loop(areas.iterator(), areasToStringTask);
	
	retval += (String)areasToStringTask.getValue();

	return retval;
    }

    public void advanceTime(double msecs) {
	double untilTime = 30.0;
	long startTime = System.currentTimeMillis();
	int steps = (int) (msecs / deltaT);
	for (int i = 0; i < steps; i++) {
	    //System.out.println("STEP " + i);

	    peripheral.step();		// step deltaT and initiates peripheral actions
	}
	double elapsed = System.currentTimeMillis() - startTime;
	System.out.println("Elapsed time: " + elapsed + " msecs (1 simulation msec = " + elapsed/msecs + " msecs).");
    }

    /**
     * Sets deltaT and then 
     * @see Network#build
     * @see Network#simulation
     * @param deltaT a <code>double</code> value
     */
    public Network(double deltaT, boolean isConcurrent) {
	this.deltaT = deltaT;
	this.isConcurrent = isConcurrent;

	setNumberFormatting();

	conceptArea = new ConceptArea(this);
	areas.add(conceptArea);

	// the Task object that will run Area.step() for all areas contained
	stepTask = new Task() {
		public void job(Object o) {
		    if (o instanceof Area)
			((Area)o).step();
		    else {
			try {
			    ((AreaInt)o).step(); // Should be done in parallel!!!
			} catch (java.rmi.RemoteException e) { 
			    e.printStackTrace();
			    throw new RuntimeException("Cannot call remote Area.step()");
			}
		    } // end of else
		}
	    };

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
     * Build the network (USED TO: and run the simulation.)
     * Calls <code>build()</code>
     * @see #build
     * @see #simulation
     */
    public void run () {
	if (isConcurrent) {
	    pT = new ParallelTask(areas) {
		    public void init() {
			// iterate over elements and associate threads
			for (Iterator i = this.a.iterator(); i.hasNext(); ) {
			    new Thread(new Objective(this, stepTask, i.next())).start();
			} // end of for (Iterator i = a.iterator(); i.hasNext(); )
		    }
		};

	    build();		// creates areas and connections
	    pT.init();		// create & start threads
	    //simulation();
	    
	} else {		// Sequential version, single thread
	    build();
	    //simulation();	    
	} // end of if-else (isConcurrent)
    }

    /**
     * To be called after everthing else is done. Prints out the network status on standard output
     * and calls conceptArea.dumpData() to create a MatLab script for viewing spike activity.
     * TODO: should call getStatus instead of toString.
     * @see Network#toString
     * @see conceptArea#dumpData
     */
    public void finale () {
	System.out.println("Network status: \n" + this.getStatus());

	// Create Matlab script about concept spike activity
	String scriptname = "spikes.m";
	try {
	    PrintWriter matlabScript = new PrintWriter(new FileWriter(scriptname)); 
	    matlabScript.print(conceptArea.dumpData());
	    matlabScript.close();
	} catch (IOException e) {
	    System.out.println("Error writing matlab script " + scriptname + ": " + e);
	}

	System.exit(0);
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
