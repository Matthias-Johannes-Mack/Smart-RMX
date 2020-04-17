package xml;

/**
 * contains the Condition Types that can be found in the XML
 */
public enum XML_ConditionTypes {
    BITCONDITION(4), BYTECONDITION(6);

    private XML_ConditionTypes(int arrayLength) {
        this.ARRAY_LENGTH = arrayLength;
    }

    /**
     * Indicates how long the parsed Array by the XML-Reader is
     */
    public final int ARRAY_LENGTH;

}
