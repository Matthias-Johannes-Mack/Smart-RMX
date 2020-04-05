package action;

/**
 * Class that extends the Action and is for the Action message
 *
 * @author Matthias Mack 3316380
 */
public class ActionMessage extends Action {
	//
	private int[] action;

	// get the constructor from the daddy
	public ActionMessage(int ID, int[] action) {
		this.action = action;
	}

	/**
	 * Getter for Action Message
	 * 
	 * @return
	 */
	public int[] getActionMesssage() {
		return action;
	}
}
