
package neuroidnet.ntr;

import neuroidnet.ntr.plots.*;
import neuroidnet.utils.*;

import java.util.*;

// $Id$
/**
 * <code>Profile</code> of <code>Mode</code> objects.
 *
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

	public ProfileTask() {
	    Iteration.loop(entrySet(), this);
	}

	public void job(Object o) {
	    Map.Entry entry = (Map.Entry)o;
	    profile.put(entry.getKey(),
			new ProfilableDouble(getVar((Neuroid.Mode)entry.getValue())));
	}

	abstract double getVar(Neuroid.Mode mode);

	public Object getValue() { return profile; }
	
	public Profile getProfile() { return (Profile)getValue(); }
    }

    public Profile getStateProfile() {
	return new ProfileTask() {
		double getVar(Neuroid.Mode mode) { return mode.getState(); }
	    }.getProfile();
    }

    public Profile getFitnessProfile() {
	return new ProfileTask() {
		double getVar(Neuroid.Mode mode) { return mode.getFitnessCounter(); }
	    }.getProfile();
    }    

    public Profile getThresholdProfile() {
	return new ProfileTask() {
		double getVar(Neuroid.Mode mode) { return mode.getThreshold(); }
	    }.getProfile();
    }

    public Profile getSuggestedThresholdProfile() {
	return new ProfileTask() {
		double getVar(Neuroid.Mode mode) { return mode.getSuggestedThreshold(); }
	    }.getProfile();
    }

    public Plot getStatePlot() {
	return new ProfilePlot("State", null, getStateProfile());
    }

    public Plot getFitnessPlot() {
	return new ProfilePlot("Fitness", null, getFitnessProfile());
    }    

    public Plot getThresholdPlot() {
	return new ProfilePlot("Threshold", null, getThresholdProfile());
    }

    public Plot getSuggestedThresholdPlot() {
	return new ProfilePlot("SuggestedThreshold", null, getSuggestedThresholdProfile());
    }

}// ModeProfile
