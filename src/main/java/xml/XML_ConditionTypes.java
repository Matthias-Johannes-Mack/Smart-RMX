package xml;

/**
 * contains the Condition Types that can be found in the XML
 */
public enum XML_ConditionTypes {
    /**
     *[Bus, Systemadddess,Bit, Bitvalue]
     */
    BIT_CONDITION(4),

    /**
     *[Bus, Systemaddress, Equals, NotEquals, Bigger, Smaller]
     */
    BYTE_CONDITION(6);

    /**
     * private constructor for the arraylengths
     * @param arrayLength
     */
    XML_ConditionTypes(int arrayLength) {
        this.ARRAY_LENGTH = arrayLength;
    }

    /**
     * Indicates how long the parsed Array by the XML-Reader is
     */
    public final int ARRAY_LENGTH;

}
