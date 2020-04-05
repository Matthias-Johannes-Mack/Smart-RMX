package action;

/**
 * Class that represents a Action, which is saved in the ActionDepot
 *
 * @author Matthias Mack 3316380
 */
public class Action {
	// Array of integers
	private int[] action;

	// Factory sets the
	public Action(int[] action) {
		this.action = action;
	}

	/**
	 * returns the action
	 * 
	 * @return
	 */
	public int[] getAction() {
		return action;
	}
}
