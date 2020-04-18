package action.actionSequence;

import action.actions.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a ActionSequence
 */
public class ActionSequence {

	/**
	 * list that contains all Actions
	 */
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
	 * Getter for the actions
	 * 
	 * @return a List of all Actions in the ActionSequence
	 */
	public List<Action> getActions() {
		return actions;
	}

	/**
	 * Method that adds an action to the ActionSequence at the end (FIFO)
	 * 
	 * @param action a action to add
	 */
	public void addAction(Action action) {
		actions.add(action);

		// increment actionCount
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

	/**
	 * @return actionCount - indicates how many actions this ActionsSequence includes
	 */
	public int getActionCount() {
		return actionCount;
	}

}
