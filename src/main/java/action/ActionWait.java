package action;

/**
 * 
 * Class for the wait Action
 * 
 * @author Matthias Mack 3316380
 */
public class ActionWait extends Action {
	private long waitTime;

	// Get the constructor from the daddy
	public ActionWait(long waitTime) {
		this.waitTime = waitTime;
	}

	/**
	 * return the wait action
	 * 
	 * @return int array
	 */
	public long getWait() {
		return waitTime;
	}
}
