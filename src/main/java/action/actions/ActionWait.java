package action.actions;

/**
 *
 * Class that represents a ActionWait
 * 
 * @author Matthias Mack 3316380
 */
public class ActionWait extends Action {

	/**
	 * waitTime that indicates the specified delay of the given ActionWait
	 */
	private long waitTime;

	/**
	 * Constructor for an ActionMessage
	 * @param waitTime specifies the delay of this ActionWait in Milliseconds
	 */
	public ActionWait(long waitTime) {
		this.waitTime = waitTime;
	}

	/**
	 *
	 * @return waitTime
	 */
	public long getWaitTime() {
		return waitTime;
	}



	/**
	 * Compares two ActionWaits
	 * @param o object to compare the current ActionWait with
	 * @return true if their waitTime is equal, false otherwise
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

		// compare their waitTime
		ActionWait that = (ActionWait) o;
		return waitTime == that.waitTime;
	}

}
