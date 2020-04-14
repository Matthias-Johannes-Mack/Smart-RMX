package action;

import java.util.Arrays;

/**
 * Class that represents a ActionMessage for a Bit Toggle in the RMX PC zentrale
 *
 * @author Matthias Mack 3316380
 */
public class ActionMessageBitToggle extends Action {

    /**
     * Integer Array that contains the info of the actionMessage
     * Format:
     * [BUS][Systemadresse][bitIndex]
     */
    private int[] actionMessageBitToggle;

    /**
     * Constructor for an ActionMessageBitToggle
     *
     * passend actionmessageBitToggle needs to have the format:
     * [BUS][Systemadresse][bitIndex]
     *
     * @param actionMessageBitToggle
     */
    public ActionMessageBitToggle(int[] actionMessageBitToggle) {
        this.actionMessageBitToggle = actionMessageBitToggle;
    }

    /**
     * @return the actionMessageBitToggle
     */
    public int[] getActionMessageBitToggle() {
        return actionMessageBitToggle;
    }

    /**
     * Compares two ActionMessages
     * @param o object to compare the current ActionMessage with
     * @return true if their actionMessage is equal, false otherwise
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

        // Compare their waitTime
        ActionMessageBitToggle that = (ActionMessageBitToggle) o;
        return Arrays.equals(actionMessageBitToggle, that.actionMessageBitToggle);
    }

}
