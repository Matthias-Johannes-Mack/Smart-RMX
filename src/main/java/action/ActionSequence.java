package action;

import java.util.ArrayList;
import java.util.List;

public class ActionSequence {

	// linked for the actions
	private ArrayList<Action> actions;

	/**
	 * indicates how many actions are contained in this ActionSequence
	 */
	private int actionCount = 0;

	/**
	 * Constructor
	 */
	public ActionSequence() {
		actions = new ArrayList<>();
	}

	/**
	 * Getter for the actionlist
	 * 
	 * @return
	 */
	public List<Action> getActions() {
		return actions;
	}

	/**
	 * Method that adds an action to the ActionSequence at the end (FIFO)
	 * 
	 * @param action
	 */
	public void addAction(Action action) {
		actions.add(action);
		actionCount++;
	}

	/**
	 * Returns the Action of the ActionSequence at the given index
	 * @param index of the action to retrieve (index starts at 0)
	 * @return Action at given index
	 */
	public Action getAction(int index) {
		return actions.get(index);
	}

	public int getActionCount() {
		return actionCount;
	}

}
