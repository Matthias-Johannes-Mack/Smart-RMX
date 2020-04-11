package xml;

import java.util.Arrays;

class XML_ActionWrapper {
    /**
     * int Array for ActionMessageBit: [Bus][Systemadress][Bit][BitValue]
     * int Array for ActionMessageByte: [Bus][Systemadress][ByteValue]
     * int Array for waitAction: [Wait time in ms]
     * int Array for Byte Increment [bus][systemadress][value]
     * int Araay for Byte Decrement [bus][systemadress][ - value]
     */
    private int[] actionArray;

    /**
     * the XML_ActionType of the given XML_ActionWrapper
     */
    private XML_ActionType type;

    /**
     * Constructor that creates a XML_ActionWrapper with the given actionArray and XML_ActionType
     * @param actionArray int array containing the data of the given XML_Actiontype
     * @param type  the XML_ActionType
     */
    public XML_ActionWrapper(int[] actionArray, XML_ActionType type) {
        this.actionArray = Arrays.copyOfRange(actionArray, 0, type.ARRAY_LENGTH);
        this.type = type;
    }

    /**
     * @return the actionArray
     */
    public int[] getActionArray() {
        return actionArray;
    }

    /**
     * @return the XML_Actiontype
     */
    public XML_ActionType getType() {
        return type;
    }
}

