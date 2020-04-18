package action;

import action.actions.Action;

import java.util.ArrayList;

/**
 * Class that contains all Actions in a list
 * guarantees that no duplicates exist:
 *
 * - ActionsMessages are equal if their message is equal
 * - ActionWaits are equal if their waitTime is equal
 *
 * @author Matthias Mack 3316380
 */
public class ActionDepot {

	// Singleton-Pattern START -----------------------------------------

	/**
	 * Singleton instance of ActionDepot
	 */
	private static ActionDepot instance = null;

	/**
	 * private constructor to prevent instantiation
	 */
	private ActionDepot() {

	}

	/**
	 * Returns singleton ActionDepot instance
	 *
	 * @return ActionDepot Singleton instance
	 */
	public static synchronized ActionDepot getActionDepot() {
		if (instance == null) {
			instance = new ActionDepot();
		}
		return instance;
	}

	// Singleton-Pattern END ________________________________________________

	/**
	 * list that contains all Actions
	 */
	private ArrayList<Action> actionDepotList = new ArrayList<>();

	/**
	 * adds an action to the ActionDepot
	 *
	 * checks if the action already exits in the ActionDepot, if so the existing Action is returned, else the Action
	 * is added to the ActionDepot an then is returned.
	 *
	 * - ActionsMessages are equal if their message is equal
	 * - ActionWaits are equal if their waitTime is equal
	 * 
	 * @param action a action to add
	 * @return action - the given Action if it does not already exist. If the action alreaady exists the corresponding
	 * 					action from the ActionDepot
	 */
	public synchronized Action addAction(Action action) {

		if (actionDepotList.contains(action)) {
			// if the given Action already exists return the corresponding Action in the ActionDepot
			int index = actionDepotList.indexOf(action);
			return actionDepotList.get(index);
		}
		// if the Action doesnt already exist in the ActionDepot: add it and return the action
		actionDepotList.add(action);
		return action;
	}

}
