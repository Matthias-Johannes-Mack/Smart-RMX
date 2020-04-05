package action;

/**
 * Class that represents a Action, which is saved in the ActionDepot
 *
 * @author Matthias Mack 3316380
 */
public abstract class Action {

	// id of the action
	protected int ID;

	// get ID
	public int getID() {
		return ID;
	}

	@Override
	public boolean equals(Object obj) {
		// change equals to match ID
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Action action = (Action) obj;
		return ID == action.ID;
	}
}
