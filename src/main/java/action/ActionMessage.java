package action;

import java.util.Arrays;

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
		this.ID = ID;
	}

	/**
	 * Getter for Action Message
	 * 
	 * @return
	 */
	public int[] getActionMesssage() {
		return action;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ActionMessage that = (ActionMessage) o;
		return Arrays.equals(action, that.action);
	}

}
