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
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        if (obj instanceof ActionMessage) {
            if (this instanceof ActionMessage) {
                ActionMessage o = (ActionMessage) this; //this
                ActionMessage object = (ActionMessage) obj;   // obj

                return object.equals(o);
            } else {
				// this => ActionWait and obj => ActionMessage
                return false;
            }
        } else {
            // wait message
            if (this instanceof ActionWait) {
            	// both are type of ActionWait
                ActionWait o = (ActionWait) this; //this
                ActionWait object = (ActionWait) obj;   // obj

                return object.equals(o);
            } else {
            	// this => ActionMessage and obj => ActionWait
                return false;
            }

        }
    }
}
