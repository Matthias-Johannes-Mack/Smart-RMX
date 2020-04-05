package action;

import java.util.ArrayList;

import bus.Bus;
import bus.BusDepot;

/**
 * Class that contains all Actions in a list
 *
 * @author Matthias Mack 3316380
 */
public class ActionDepot {
	 // Singleton-Pattern START -----------------------------------------

    /**
     * Singleton instance of BusDepot
     */
    private static ActionDepot instance = null;

    /**
     * private constructor to prevent instantiation
     */
    private ActionDepot() {

    }

    /**
     * Returns singleton BusDepot instance
     *
     * @return BusDepot Singleton instance
     */
    public static synchronized ActionDepot getActionDepot() {
        if (instance == null) {
            instance = new ActionDepot();
        }
        return instance;
    }

    // Singleton-Pattern END ________________________________________________

    
    private ArrayList<Action> actionDepot = new ArrayList<>();


   
    public synchronized Action getAction(int actionID) {
        return actionDepot.get(actionID);
    }
    /**
     * 
     * @param actionID
     * @return
     */
    private synchronized boolean actionExists(int[] action) {
    	if(actionDepot.contains(actionID)) {
    		return true;
    	}
        return false;
    }

    public synchronized void removeAction(int actionID) {
        actionDepot.remove(actionID);
    }

    public synchronized void clearTrain() {
        actionDepot.clear();
    }
}
