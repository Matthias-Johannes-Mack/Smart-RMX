package xml.xmlutilities;

/**
 * Enum to distinguish the different Types of Actions that are supported and their action array (int[]) length
 */
public enum XML_ActionType {
    /**
     * [bus][systemadress][ - value]
     */
    DECREMENT(3),

    /**
     * [bus][systemadress][value]
     */
    INCREMENT(3),

    /**
     * [Bus][Systemadress][ByteValue]
     */
    BYTEMESSAGE(3),

    /**
     * [Bus][Systemadress][Bit][BitValue]
     */
    BITMESSAGE(4),

    /**
     * [Wait time in ms]
     */
    WAIT(1),

    /**
     * [Bus][Systemadress][Bit]
     */
    BITTOGGLE(3);


    /**
     * indicator of how log the max length of an type is
     */
    public static final int MAX_LENGTH_OF_ACTION_ARRAY = 4;

    /**
     * private constructor for the arraylengths
     *
     * @param arrayLength
     */
    XML_ActionType(int arrayLength) {
        this.ARRAY_LENGTH = arrayLength;
    }

    /**
     * Indicates how long the parsed Array by the XML-Reader is
     */
    public final int ARRAY_LENGTH;
}




