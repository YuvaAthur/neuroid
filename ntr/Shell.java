package neuroidnet.ntr;

// $Id$
/**
 * New reason: to bootstrap BeanShell environment with predefined
 * methods for neuroidnet simulations. Loads a rc.bsh file from the
 * current dir for this objective.
 *
 * OBSOLETE: See BeanShell related work for same purpose.
 * Initial effort spent to add functionality to Network methods,
 * later to be replaced by NeuroShell (extending BeanShell).
 * <p>Debugger style shell wrapper to drive the neuroidal simulator.
 * Contains a read-eval-print loop
 * for running the simulation and observing its status. 
 *
 * <p>Created: Mon Oct 22 00:04:24 2001
 * <p>Modified: $Date$
 *
 * @author Cengiz Gunay
 * @version $Revision$ for this file.
 * @deprecated See BeanShell related methods in <code>Network</code>.
 * @see Network
 */

import java.io.*;
import java.lang.*;
import java.util.*;
import bsh.Interpreter;
import bsh.CommandLineReader;

public class Shell {
    String rcFile = "rc.bsh";
    Interpreter bsh;

    public Shell(boolean swingConsole) {
	if (swingConsole) {
	    bsh = new Interpreter(); 
	    setNeuroidNetEnvironment();
	    try {
		bsh.eval("desktop( this.namespace ); ");
	    } catch (bsh.EvalError e) {
		System.out.println("Warning: Cannot invoke the BeanShell Swing desktop...\n" +
				   "Falling into command line mode.");
		commandLineMode();	    
	    } // end of try-catch
	} else {
	    commandLineMode();
	} // end of else

    }

    void setNeuroidNetEnvironment() {
	try {
	    bsh.eval("source(\"" + rcFile + "\");"); 
	} catch (bsh.EvalError e) {
	    System.out.println("Warning: Cannot set the NeuroidNet environment for BeanShell.\n" +
			       "File not found: " + rcFile);
	}
    }

    void commandLineMode() {
	// Workaround for JDK bug 4071281, where system.in.available() 
	// returns too large a value. This bug has been fixed in JDK 1.2.
	InputStream src;
	if ( System.getProperty("os.name").startsWith("Windows") 
	     && System.getProperty("java.version").startsWith("1.1."))
	    {
		src = new FilterInputStream(System.in) {
			public int available() throws IOException {
			    return 0;
			}
		    };
	    }
	else
	    src = System.in;

	Reader in = new CommandLineReader( new InputStreamReader(src));
	bsh = new Interpreter( in, System.out, System.err, true );
	setNeuroidNetEnvironment();
	bsh.run();
    }

    static void usage() {
	System.out.println("Starts a BeanShell interpreter for the NeuroidNet simulator.\n" +
			   "Usage:\n" +
			   "\t-h: This message.\n" +
			   "\t-c: Do not invoke the Swing console, only command line interpreter \n" +
			   "\t    (can be used in Xemacs with java-mode indentation.)\n\n");
	
    }

    public static void main (String[] args) {

	if (args.length > 0 && args[0].toLowerCase().equals("-h")) {
	    usage();
	    System.exit(0);
	}
	
	boolean swingConsole = true;
	if (args.length > 0 && args[0].toLowerCase().equals("-c")) 
	    swingConsole = false;

	new Shell(swingConsole);
    } // end of main ()

    
/*    BufferedReader in;
    ClassLoader loader;

    public Shell() {
	
	in = new BufferedReader(new InputStreamReader(System.in));
	loader = ClassLoader.getSystemClassLoader();

	read_eval_print_loop();
 
    }

    class Directive {
	String name, description;

	public Directive(String name, String description) {
	    this.name = name;
	}

	public abstract void run(StringTokenizer st);
	public abstract String usage();
    }

    // returns a correct type object by parsing the string token.
    // consults the symbol table for variables associated with certain types
    public static Object evaluate(String token) {

	// a subsumption architecture 
	// TODO: first try the symbol table lookup
	// TODO: use Object NumberFormat.parse(String) ?
	try {
	    return Double(token); // highest prioority
	} catch (NumberFormatException e) { // failed, continue
	}

	try {
	    return Integer(token); 
	} catch (NumberFormatException e) { // failed, continue
	}

	// Is it a quoted string?
	//	if ()
    }

    void read_eval_print_loop() {
	// new network, requires one argument: class name that extends ntr.Network
	Directive newNetwork = new Directive("new", "Create a new network from a class.") {
		public void run(StringTokenizer st) {
		    if (!st.hasMoreTokens()) {
			System.out.println("Error: Directive 'new' argument missing.");
			continue;
		    }
		    try {
			Class netClass = loader.loadClass(st.nextToken());
		    } catch (ClassNotFoundException e) {
			System.out.println("Error: Class " + classname + " not found.");
			continue;
		    }
		    Constructor[] conss = netClass.getConstructors();
		    Iteration.loop(Arrays.asList(conss).iterator(),
				   new Task() {
					   public void job(Object o) {
					       // get name and param types of cons and match
					       // with st. If ok, instantiate.
					       Constructor c = (Constructor) o;
					       String name = c.getName();
					       Class params[] = c.getParameterTypes(); 

					       // for all remaining elements in st do:
					       // params[].isAssignableFrom(el.getClass())
					   }
				       });
		}
	    };

	while (true) {
	    String command = in.readLine(); // read line from std input
	    StringTokenizer st = new StringTokenizer(command);
	    if (!st.hasMoreTokens()) {
		System.out.println("Error: Nothing on command line?");
		continue;
	    }
	    String directive = st.nextToken(); // first token

	    if (directive.equals(newNetwork.name)) { 
		newNetwork.run(st);
	    }

	}
    }

    public static void main (String[] args) {
	new Shell();
    } // end of main ()
*/    
}
