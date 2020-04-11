package action;

import java.util.Arrays;

/**
 * class that represents an action that will send a message to the RMX PC Zentrale that changes the status of a whole byte
 */
public class ActionMessageByte extends Action{

    /**
     * Integer Array that contains the info of the actionMessageByte
     * Format:
     * [BUS][Systemadresse][byteValue]
     */
    private int[] actionMessageByte;

    /**
     * Constructor for an ActionMessageByte
     *
     * passend actionmessage needs to have the format:
     * [BUS][Systemadresse][ByteValue]
     *
     * @param actionMessageByte
     */
    public ActionMessageByte(int[] actionMessageByte) {
        this.actionMessageByte = actionMessageByte;
    }

    /**
     * Getter for ActionMessageByte
     *
     * @return actionMessage
     */
    public int[] getActionMessageByte() {
        return actionMessageByte;
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
        ActionMessageByte that = (ActionMessageByte) o;
        return Arrays.equals(actionMessageByte, that.actionMessageByte);
    }

}
