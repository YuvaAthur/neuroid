
package neuroidnet.phasesegregator;

import neuroidnet.ntr.*;
import neuroidnet.periphery.*;

import java.io.*;
import java.util.*;

import edu.ull.cgunay.utils.*;


// $Id$
/**
 * Creates a random input sequence out of possible input patterns
 * (objects). Designed to be instantiated separately from the Network
 * (not in build() anymore). 
 *
 * <p> Also evaluates the network after training to check for the
 * quality of correct and spurious concepts memorized.
 *
 * <p> TODO: get only one percept from I1 and lower M1's threshold to
 * allow recruitment.
 *
 * <p>Created: Fri Oct 18 14:12:22 2002
 * <p>Modified: $Date$
 *
 * @author <a href="mailto:cengiz@ull.edu">Cengiz Gunay</a>
 * @version $Revision$ for this file.
 */

public class StatisticalInputSequence extends Peripheral  {
    
    /**
     * Can be manipulated by beanshell scripts under <code>scripts/</code>.
     *
     */
    int numberOfObjects = 0;

    int
	numberOfNeuroids = ((Network)network).numberOfNeuroids,
	replication = ((Network)network).replication,
	numberOfMedialAreas = ((Network)network).numberOfMedialAreas,
	numberOfItemsPerArea = ((Network)network).numberOfItemsPerArea;

    /**
     * In order to be used from bsh: As constructor only take
     * necessary parameters, like connect to a network. Then set
     * properties from bsh, and then call build to validate the
     * object, which should call initEvents.. Needs changing previous
     * architecture.
     *
     * @param network a <code>Network</code> value
     */
    public StatisticalInputSequence (Network network) {
	super(network, network.inputAreas, network.numberOfItemsPerArea, network.segregation);
    }

    class SimObject implements Input, Serializable {
	Input[] percepts;
	ComparableSortedSet conceptSet = new ComparableSortedSet();
	Concept concept;
	
	SimObject() {
	    //percepts = new Vector();
	}

	/**
	 * Loops over all <code>percepts</code> and fires them.
	 *
	 */
	public void fire() {
	    new UninterruptedIteration() {
		public void job(Object o) {
		    ((Input)o).fire();
		}
	    }.loop(percepts);
	}

	/**
	 *
	 * @return <description>
	 */
	public String toString() {
	    return getClass().getName() + ": Concept = " + conceptSet;
	}
	
	/**
	 * Checks equality according to <code>conceptSet</code>s.
	 *
	 * @param that an <code>SimObject</code> value
	 * @return a <code>boolean</code> value
	 */
	public boolean equals(Object that) {
	    return conceptSet.compareTo(((SimObject)that).conceptSet) == 0;
	}
	
    }

    /**
     * Objects to be fired in the input sequence.
     *
     */
    Vector simObjects;

    /**
     * Builds properties of the peripheral object. Here, prepare
     * random objects to be registered.
     *
     */
    public void build() {

	// if not initialized (lame..)
	if (numberOfObjects == 0) 
	    // Number of concepts to present to the network
	    numberOfObjects = numberOfNeuroids / replication;
	

	// BUG: Objects are not inspected for uniqueness, leads to 
	// misinterpretation of network capacity.

	// TODO: make the following a set capable of contains() for checking existing objects
	simObjects = new Vector(numberOfObjects);

	for (int objectNum = 0; objectNum < numberOfObjects; objectNum++) {
	    final SimObject object = new SimObject();
	    object.percepts = new Input[numberOfMedialAreas + 1];

	    int retries = 30;
	    while (--retries > 0) {
		int firstOne = (int) (numberOfItemsPerArea * Math.random());
		int secondOne =
		    (int) ((firstOne + ((numberOfItemsPerArea - 1) * Math.random() + 1)) % 
			   numberOfItemsPerArea);

		object.percepts[0] = ((Input[])percepts.get(inputAreas[0]))[firstOne];
		object.percepts[1] = ((Input[])percepts.get(inputAreas[0]))[secondOne];

		for (int areaNum = 1; areaNum < numberOfMedialAreas; areaNum++) {
		    object.percepts[areaNum + 1] =
			((Input[])percepts.get(inputAreas[areaNum]))[(int) (numberOfItemsPerArea * Math.random())];
		} // end of for (areaNum = 0; areaNum < numberOfMedialAreas; areaNum++)
	    
		new UninterruptedIteration() {
		    public void job(Object o) {
			object.conceptSet.addAll(((ArtificialConcept)((Neuroid)o).getConcept()).getConceptSet());
		    }
		}.loop(object.percepts);

		if (!simObjects.contains(object)) break;

		// Retrying, so clean the conceptSet
		object.conceptSet.clear();
	    }  // end of while (retries-- > 0)

	    if (retries == 0) 
		throw new Error("Cannot find another unique object! " +
				getProperties());
	    
	    simObjects.add(object);
	}
	
	initEvents();
    }

    

    void initEvents() {
	// Loop for all objects and register events, 
	new UninterruptedIteration() {
	    /** Time offset */
	    int offset = 0;
	    
	    public void job(Object o) {
		final SimObject object = (SimObject)o;

		// Register a peripheral input event
		events.put(new Double(offset), new Task() {
			// Iterator for firing percepts
			final UninterruptedIteration firePercepts =
			    new UninterruptedIteration() {
				public void job(Object o) { ((Input)o).fire(); }
			    };

			public void job(Object o) {
			    // fire all percepts in object
			    firePercepts.loop(object.percepts);
			}});

		// Separate objects in time
		offset += segregation;
	    }
	    }.loop(simObjects);	

	super.initEvents();
    }

    /**
     * After simulation, checks if desired concepts were allocated in
     * the NTR during simulation. For all objects, check if concepts
     * has been formed and how many members do they have. Sets
     * <code>SimObject.concept</code> 
     *
     * <p>Simplification: Check only
     * final concepts (anyway if intermediate concepts are not
     * properly defined, final concepts cannot be formed.)  
     *
     * <p>TODO: Produce a graph?
     */
    public void evaluateResults() {
	new UninterruptedIteration() {
	    public void job(Object o) {
		SimObject object = (SimObject)o;
		object.concept = (Concept) network.getConceptArea().get(object.conceptSet);
	    }
	}.loop(simObjects);
    }

    /**
     * Calculates a quality value in the range [0, 1], 1 indicating
     * full replication has been reached in recruitment for each
     * intended simulation object.
     *
     * @param concepts Concept set for which the quality is to be calculated.
     * @return 1 if all concepts contained <code>replication</code>
     * elements, 0 if any of the concepts were missing, and something
     * in between otherwise.
     */
    public double qualityOfConcepts(final Set concepts) {
	return
	    ((Double) new TaskWithReturn() {
		    double totalQuality = 0.0;

		    public void job(Object o) /*throws BreakOutOfIterationException*/ {
			ArtificialConcept concept = (ArtificialConcept)o;
			if (concept != null) 
			    //throw new BreakOutOfIterationException();
			    totalQuality += concept.getSynapses().size();
		    }

		    public Object getValue() {
			try {
			    Iteration.loop(concepts, this);			     
			} catch (BreakOutOfIterationException e) {
			    totalQuality = 0; // in case of any concepts are missing!
			} // end of try-catch

			if (concepts.size() > 0) 
			    return new Double(totalQuality / (replication * concepts.size()));
			else 
			    return new Double(0);
		    }
		}.getValue()).doubleValue();	
    }

    /**
     * Computes the set of spurious concepts by pruning from the
     * contents of the <code>ConceptArea</code> the subsets of all
     * <code>simObjects</code>.
     *
     * @return Set of all spurious concepts.
     */
    public SortedSet spuriousConcepts() {
	final SortedSet allConceptsSet = new TreeSet(network.getConceptArea().neuroids);

	// for all objects
	new UninterruptedIteration() {
	    public void job(Object o) { 
		final SimObject object = (SimObject) o;
		new UninterruptedIteration() {
		    public void job(Object o) throws RemoveFromIterationException { 
			Set conceptSet = ((ArtificialConcept)o).getConceptSet();

			// Remove if it's a primitive (sensory) concept
			if (conceptSet.size() == 1) 
			    throw new RemoveFromIterationException();
			
			// Remove if it's a subset of the object
			if (object.conceptSet.containsAll(conceptSet))
			    throw new RemoveFromIterationException();
		    }
		}.loop(allConceptsSet);
	    }
	}.loop(simObjects);

	return allConceptsSet;
    }

    /**
     * Builds a sorted set from <code>simObjects</code>.
     *
     * @return Set of recruited correct concepts.
     */
    public SortedSet correctConcepts() {
	final SortedSet conceptsSet = new TreeSet();

	// for all objects
	new UninterruptedIteration() {
	    public void job(Object o) {
		SimObject object = (SimObject)o;
		if (object.concept != null) 
		    conceptsSet.add(object.concept);
	    }
	}.loop(simObjects);

	return conceptsSet;
    }

    public String getProperties() {
	return
	    this + ": numberOfObjects = " + numberOfObjects + 
	    ", simObjects = " + new StringTask("{\n", "}") {
		    public void job(Object o) {
			super.job(o + "\n"); 
		    }
		}.getString(simObjects); 
    }

    
}// StatisticalInputSequence
