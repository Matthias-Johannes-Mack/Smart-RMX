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
	 * checks if actions exists in the ActionDepot
	 * 
	 * @param action
	 * @return Returns the action, -1 if action does not exist
	 */
	public synchronized Action actionExists(Action action) {
		int index;
		// if action exists return the action
		if (actionDepot.contains(action)) {
			index = actionDepot.indexOf(action);
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

	/**
	 * adds an action to the ActionDepot.If the action already exists (specified by
	 * Action.equal()) returns id of existing action and does not add given action
	 * again. This ensures every action only exists exactly one time in the
	 * ActionDepot.
	 *
	 * @param action a action to add
	 * @return index of the action
	 */
	public synchronized int addAction(Action action) {

		if (!actionDepot.contains(action)) {
			// action doesnt exist
			actionDepot.add(action); // add action
		}

		return actionDepot.indexOf(action);
	}
}
