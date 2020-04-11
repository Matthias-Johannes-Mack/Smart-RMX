package xml;

/**
 * Enum to distinguish the different Types of Actions that are supported and their action array (int[]) length
 */
public enum XML_ActionType {
DECREMENT(3), INCREMENT(3), BYTEMESSAGE(3), BITMESSAGE(4), WAIT(1), BITTOGGLE(3);

    private XML_ActionType(int arrayLength) {
        this.ARRAY_LENGTH = arrayLength;
    }

    /**
     * Indicates how long the parsed Array by the XML-Reader is
     */
    public final int ARRAY_LENGTH;
}



