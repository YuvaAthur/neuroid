import java.lang.*;
import java.util.*;
import Utils.*;

/**
 * Guinea.java
 *
 *
 * Created: Mon Dec  4 23:47:39 2000
 *
 * @author Cengiz Gunay
 * @version
 */

public class Guinea  {
    volatile ParallelTask pT;

    public Guinea () {
	Vector a = new Vector();
	a.add("A");
	a.add("B");
	a.add("C");
	
	pT = new ParallelTask(a) {
		Task task;
		public void init() {
		    task = new Task() {
			    public void job(Object o) {
				System.out.println("something: " + o);
			    }
			};
		    
		    // iterate over elements and associate threads
		    for (Iterator i = this.a.iterator(); i.hasNext(); ) {
			new Thread(new Objective(this, task, i.next())).start();
		    } // end of for (Iterator i = a.iterator(); i.hasNext(); )
		}
	    };

	pT.init();		// create & start threads
	synchronized (pT) {
	    step();
	    step();
	    step();
	    step();
	    step();
	    step();
	}
	System.exit(0);
    }


    public static void main (String[] args) {
	new Guinea();
    } // end of main ()

    void step() {
	try {
	    while (pT.waitcount < 3) {
		System.out.println("Waiting for others to wait!");
		pT.wait(10);
	    }
	} catch (InterruptedException e) {
	    System.out.println("interrupted!!!" + e);
	    e.printStackTrace();
	}

	pT.notifyAll();
	System.out.println("notified all!");

	try {
	    //System.out.println("Waiting for others to finish!");
	    //pT.wait();		// Give up lock, become first one in queue!

	    while (pT.runcount > 0) {
		System.out.println("Waiting for others to finish!");
		pT.wait(10);
	    }
	} catch (InterruptedException e) {
	    System.out.println("interrupted!!!" + e);
	    e.printStackTrace();
	}
	pT.reset();

    }

}// Guinea
