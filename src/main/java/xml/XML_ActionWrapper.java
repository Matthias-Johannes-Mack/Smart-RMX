package xml;

import java.util.Arrays;

/**
 * contains the type of an action and its action array read in from the xml file
 */
class XML_ActionWrapper {
    /**
     * int Array containing the array for a the specific type of the action
     *
     * int Array for ActionMessageBit: [Bus][Systemadress][Bit][BitValue]
     * int Array for ActionMessageByte: [Bus][Systemadress][ByteValue]
     * int Array for waitAction: [Wait time in ms]
     * int Array for Byte Increment [bus][systemadress][value]
     * int Array for Byte Decrement [bus][systemadress][ - value]
     */
    private int[] actionArray;

    /**
     * the XML_ActionType of the action
     */
    private XML_ActionType type;

    /**
     * Constructor that creates a XML_ActionWrapper with the given actionArray and XML_ActionType
     * @param actionArray int array containing the data of the given XML_Actiontype
     * @param type the XML_ActionType
     */
    public XML_ActionWrapper(int[] actionArray, XML_ActionType type) {
        this.actionArray = Arrays.copyOfRange(actionArray, 0, type.ARRAY_LENGTH);
        this.type = type;
    }

    /**
     * getter for the action array
     * @return the actionArray
     * ActionMessageBit: [Bus][Systemadress][Bit][BitValue]
     * ActionMessageByte: [Bus][Systemadress][ByteValue]
     * waitAction: [Wait time in ms]
     * Byte Increment [bus][systemadress][value]
     * Byte Decrement [bus][systemadress][ - value]
     */
    public int[] getActionArray() {
        return actionArray;
    }

    /**
     * getter for action type
     * @return the XML_Actiontyp
     */
    public XML_ActionType getType() {
        return type;
    }
}

