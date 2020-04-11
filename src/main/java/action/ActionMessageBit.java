package action;

import java.util.Arrays;

/**
 * Class that represents a ActionMessage for a Bit Change in the RMX PC zentrale
 *
 * @author Matthias Mack 3316380
 */
public class ActionMessageBit extends Action {

	/**
	 * Integer Array that contains the info of the actionMessage
	 * Format:
	 * [BUS][Systemadresse][bitIndex][bitValue]
	 */
	private int[] actionMessageBit;

	/**
	 * Constructor for an ActionMessage
	 *
	 * passend actionmessage needs to have the format:
	 * [BUS][Systemadresse][bitIndex][bitValue]
	 *
	 * @param actionMessage
	 */
	public ActionMessageBit(int[] actionMessage) {
		this.actionMessageBit = actionMessage;
	}

	/**
	 * Getter for Action Message
	 * 
	 * @return actionMessage
	 */
	public int[] getActionMessageBit() {
		return actionMessageBit;
	}

	/**
	 * Compares two ActionMessages
	 * @param o object to compare the current ActionMessage with
	 * @return true if their actionMessage is equal, false otherwise
	 */
	@Override
	public boolean equals(Object o) {

		// if same object
		if (this == o) {
			return true;
		}

		// if null or not even same class
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		// Compare their waitTime
		ActionMessageBit that = (ActionMessageBit) o;
		return Arrays.equals(actionMessageBit, that.actionMessageBit);
	}

}
