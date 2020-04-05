package action;

import java.util.Objects;

/**
 * 
 * Class for the wait Action
 * 
 * @author Matthias Mack 3316380
 */
public class ActionWait extends Action {
	private long waitTime;

	// Get the constructor from the daddy
	public ActionWait(int ID, long waitTime) {
		this.waitTime = waitTime;
		this.ID = ID;
	}

	/**
	 * return the wait action
	 * 
	 * @return int array
	 */
	public long getWait() {
		return waitTime;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()){
			return false;
		}

		ActionWait that = (ActionWait) o;
		return waitTime == that.waitTime;
	}

}
