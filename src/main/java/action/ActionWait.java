package action;

/**
 * 
 * Class for the wait Action
 * 
 * @author Matthias Mack 3316380
 */
public class ActionWait extends Action {
	// Get the constructor from the daddy
	public ActionWait(int[] action) {
		super(action);
	}

	/**
	 * return the wait action
	 * 
	 * @return int array
	 */
	public int[] getWait() {
		return action;
	}
}
