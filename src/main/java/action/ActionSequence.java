package action;

import java.util.ArrayList;

public class ActionSequence {
	// arraylist for the actions
	private ArrayList<Action> actions;

	/**
	 * Constructor
	 * 
	 * @param actions
	 */
	public ActionSequence() {
		actions = new ArrayList<>();
	}

	/**
	 * Getter for the actionlist
	 * 
	 * @return
	 */
	public ArrayList<Action> getActions() {
		return actions;
	}

	/**
	 * Method that adds an action to the ActionSequence at the end (FIFO)
	 * 
	 * @param action
	 */
	public void addAction(Action action) {
		actions.add(action);
	}
}
