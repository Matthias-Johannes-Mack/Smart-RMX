package xml;

public enum XML_ActionType {
DECREMENT(3), INCREMENT(3), BYTEMESSAGE(3), BITMESSAGE(4), WAIT(1);

    private XML_ActionType(int arrayLength) {
        this.ARRAY_LENGTH = arrayLength;
    }

    public final int ARRAY_LENGTH;
}



