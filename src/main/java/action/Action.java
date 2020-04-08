package action;

/**
 *
 * abstract class as template for an action
 *
 * currently there are two child classes:
 * - ActionMessage
 * - ActionWait
 *
 * @author Matthias Mack 3316380
 */
public abstract class Action {

	/**
	 * Compares two Action Objects by the following logic:
	 * - ActionWait and ActionMessage are not equal
	 * - ActionWaits are equal if their waitTime is equal
	 * - ActionMessages are equal if their messages are equal
	 *
	 * @param obj object to compare the current object with
	 * @return true if ActionWaits have the same waitTime or ActionMessages have the same message, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {

		// if same object
		if (this == obj) {
			return true;
		}

		// if null or not even same class
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		// if obj is a ActionMessage
		if (obj instanceof ActionMessage) {

			if (this instanceof ActionMessage) {
				// this is also a ActionMessage => compare two ActionMessages
				ActionMessage o = (ActionMessage) this; // this
				ActionMessage object = (ActionMessage) obj; // obj

				return object.equals(o);
			} else {
				// this => ActionWait and obj => ActionMessage
				return false;
			}
		} else {
			// obj is a ActionWait
			if (this instanceof ActionWait) {
				// this is also a WaitAction => compare two ActionWait
				ActionWait o = (ActionWait) this; // this
				ActionWait object = (ActionWait) obj; // obj

				return object.equals(o);
			} else {
				// this => ActionMessage and obj => ActionWait
				return false;
			}

		}
	}
}
