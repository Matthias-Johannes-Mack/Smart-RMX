package xml;

import java.util.Arrays;

class XML_ActionWrapper {
    /**
     * Integer Array for ActionMessageBit: [Bus][Systemadress][Bit][BitValue]
     * Integer Array for ActionMessageByte: [Bus][Systemadress][ByteValue]
     * Integer Array for waitAction: [Wait time in ms]
     * Integer Array for Byte Increment [bus][systemadress][value]
     * Integer Araay for Byte Decrement [bus][systemadress][ - value]
     */

    private int[] actionArray;
    private XML_ActionType type;

    public XML_ActionWrapper(int[] actionArray, XML_ActionType type) {
        this.actionArray = Arrays.copyOfRange(actionArray, 0, type.ARRAY_LENGTH);
        this.type = type;
    }

    public int[] getActionArray() {
        return actionArray;
    }

    public XML_ActionType getType() {
        return type;
    }
}

