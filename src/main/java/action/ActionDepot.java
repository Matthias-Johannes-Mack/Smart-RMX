package action;

import java.util.ArrayList;

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
	 * checks if actions exists in the ActionDepot
	 * 
	 * @param action
	 * @return Returns the action, -1 if action does not exist
	 */
	public synchronized Action addAction(Action action) {

		// if action exists return the action
		if (actionDepot.contains(action)) {
			int index = actionDepot.indexOf(action);
			return actionDepot.get(index);
		}
		// if not add action to the depot & return action
		actionDepot.add(action);
		return action;
	}

	/**
	 * removes a action
	 * 
	 * @param actionID
	 */
	public synchronized void removeAction(int actionID) {
		actionDepot.remove(actionID);
	}

	/**
	 * clears the Depot
	 */
	public synchronized void clearActionDepot() {
		actionDepot.clear();
	}
}
