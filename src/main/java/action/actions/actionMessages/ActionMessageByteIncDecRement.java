package action.actions.actionMessages;

import action.actions.Action;

import java.util.Arrays;

/**
 * class that represents an action that will send a message to the RMX PC Zentrale that increments or decrements the value of a specific byte
 */
public class ActionMessageByteIncDecRement extends Action {
    /**
     * Integer Array that contains the info of the actionMessageByte
     * Format:
     * [BUS][Systemadresse][IncDecValue]
     * IncDecValue if negative decrement, positive  increment
     */
    private int[] actionMessageByteIncDecRement;

    /**
     * Constructor for an ActionMessageByteIncDecRement
     * @param incrementDecrementValue specifies the number the byte should be incremented or decremented
     */
    public ActionMessageByteIncDecRement(int[] incrementDecrementValue) {
        this.actionMessageByteIncDecRement = incrementDecrementValue;
    }

    /**
     * gets the actionsArray of the actionMessageByteIncDecRement
     * @return int Array [BUS][Systemadresse][IncDecValue]
     */
    public int[] getActionMessageByteIncDecRement() {
        return actionMessageByteIncDecRement;
    }



    /**
     * Compares two ActionMessageByteIncDecRement
     * @param o object to compare the current ActionMessageByteIncDecRement with
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

        ActionMessageByteIncDecRement that = (ActionMessageByteIncDecRement) o;
        return Arrays.equals(actionMessageByteIncDecRement, that.actionMessageByteIncDecRement);
    }

}
