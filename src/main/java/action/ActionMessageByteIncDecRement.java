package action;

import java.util.Arrays;

public class ActionMessageByteIncDecRement extends Action {
    /**
     * Integer Array that contains the info of the actionMessageByte TODO
     * Format:
     * [BUS][Systemadresse][IncDecValue]
     * IncDecValue if negative decrement, positive = increment
     */
    private int[] actionMessageByteIncDecRement;

    /**
     * Constructor for an ActionMessageByteIncrement
     * @param incrementValue specifies the numberthat should be added to the byte
     */
    public ActionMessageByteIncDecRement(int[] incrementValue) {
        this.actionMessageByteIncDecRement = incrementValue;
    }

    /**
     *
     * @return waitTime
     */
    public int[] getActionMessageByteIncDecRement() {
        return actionMessageByteIncDecRement;
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

        ActionMessageByteIncDecRement that = (ActionMessageByteIncDecRement) o;
        return Arrays.equals(actionMessageByteIncDecRement, that.actionMessageByteIncDecRement);
    }

}
