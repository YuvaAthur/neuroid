
package neuroidnet.ntr;

import edu.ull.cgunay.utils.plots.*;
import edu.ull.cgunay.utils.*;

import java.util.*;

// $Id$
/**
 * <code>Profile</code> of <code>Mode</code> objects.  
 *
 * <P>TODO: Make a method that takes a string argument as the property
 * name and uses reflection to lookup values of it in
 * <code>Mode</code> (or especially its subclasses) instances to
 * create a profile.
 *
 * <p>Created: Sat Apr 13 15:31:40 2002
 * <p>Modified: $Date$
 *
 * @see Neuroid.Mode
 * @author <a href="mailto:">Cengiz Gunay</a>
 * @version $Revision$ for this file.
 */

public class ModeProfile extends Profile  {
    public ModeProfile (Profilable entity, Object time) {
	super(entity, time);
    }

    abstract class ProfileTask implements TaskWithReturn {
	Profile profile = new Profile();

	public ProfileTask() { }

	public void job(Object o) {
	    Map.Entry entry = (Map.Entry)o;
	    profile.put(entry.getKey(),
			new ProfilableDouble(getVar((Neuroid.Mode)entry.getValue())));
	}

	abstract double getVar(Neuroid.Mode mode);

	public Object getValue() { UninterruptedIteration.loop(entrySet(), this); return profile; }
	
	public Profile getProfile() { return (Profile)getValue(); }
    }

    public Profile getStateProfile() {
	return new ProfileTask() {
		double getVar(Neuroid.Mode mode) { return mode.getState(); }
	    }.getProfile();
    }

    public Profile getThresholdProfile() {
	return new ProfileTask() {
		double getVar(Neuroid.Mode mode) { return mode.getThreshold(); }
	    }.getProfile();
    }

    public Profile getFitnessProfile() {
	return new ProfileTask() {
		double getVar(Neuroid.Mode mode) { return ((SRMNeuroid.Mode)mode).getFitnessCounter(); }
	    }.getProfile();
    }    

    public Profile getSuggestedThresholdProfile() {
	return new ProfileTask() {
		double getVar(Neuroid.Mode mode) { return ((SRMNeuroid.Mode)mode).getSuggestedThreshold(); }
	    }.getProfile();
    }

    public Plot getStatePlot() {
	return new ProfilePlot("State", null, getStateProfile());
    }

    public Plot getThresholdPlot() {
	return new ProfilePlot("Threshold", null, getThresholdProfile());
    }

    public Plot getFitnessPlot() {
	return new ProfilePlot("Fitness", null, getFitnessProfile());
    }    

    public Plot getSuggestedThresholdPlot() {
	return new ProfilePlot("SuggestedThreshold", null, getSuggestedThresholdProfile());
    }

}// ModeProfile
