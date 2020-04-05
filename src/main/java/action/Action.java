package action;

/**
 * Class that represents a Action, which is saved in the ActionDepot
 *
 * @author Matthias Mack 3316380
 */
public class Action {
	// Action [Bus, SystemAddress, Bit, Bitvalue]
	protected int[] action;

	// Factory sets the
	public Action(int[] action) {
		this.action = action;
	}
}
