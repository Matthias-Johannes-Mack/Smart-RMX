package action.actions;

import action.actions.actionMessages.ActionMessageBit;
import action.actions.actionMessages.ActionMessageBitToggle;
import action.actions.actionMessages.ActionMessageByte;
import action.actions.actionMessages.ActionMessageByteIncDecRement;

/**
 * abstract class as template for an action
 * <p>
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
        if (obj instanceof ActionMessageBit) {

            if (this instanceof ActionMessageBit) {
                // obj is a ActionMessageBit and this is a ActionMessageBit
                ActionMessageBit o = (ActionMessageBit) this; // this
                ActionMessageBit object = (ActionMessageBit) obj; // obj

                return object.equals(o);
            }
        } else if (obj instanceof ActionWait) {

            if (this instanceof ActionWait) {
                // obj is a ActionWait and this is a ActionWait
                ActionWait o = (ActionWait) this; // this
                ActionWait object = (ActionWait) obj; // obj

                return object.equals(o);
            }

        } else if (obj instanceof ActionMessageByte) {
            if (this instanceof ActionMessageByte) {
                // obj is a ActionMessageByte and this is a ActionMessageByte
                ActionMessageByte o = (ActionMessageByte) this; // this
                ActionMessageByte object = (ActionMessageByte) obj; // obj

                return object.equals(o);
            }
        }  else if (obj instanceof ActionMessageByteIncDecRement) {
            if (this instanceof ActionMessageByteIncDecRement) {
                // obj is a ActionMessageByteDecrement and this is a ActionMessageByteDecrement
                ActionMessageByteIncDecRement o = (ActionMessageByteIncDecRement) this; // this
                ActionMessageByteIncDecRement object = (ActionMessageByteIncDecRement) obj; // obj

                return object.equals(o);
            }
        } else if (obj instanceof ActionMessageBitToggle) {
            if (this instanceof ActionMessageBitToggle) {
                // obj is a ActionMessageByteDecrement and this is a ActionMessageByteDecrement
                ActionMessageBitToggle o = (ActionMessageBitToggle) this; // this
                ActionMessageBitToggle object = (ActionMessageBitToggle) obj; // obj

                return object.equals(o);
            }
        }

        return false;
    }
}
