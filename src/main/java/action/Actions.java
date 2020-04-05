package action;

import java.util.ArrayList;

public class Actions {
	// arraylist for the actions
	private ArrayList<Action> actions;
	/**
	 * Constructor
	 * @param actions
	 */
	public Actions(ArrayList<Action> actions) {
		this.actions = actions;
	}
	/**
	 * Getter for the actionlist
	 * @return
	 */
	public ArrayList<Action> getActions() {
		return actions;
	}
	
}
