package schedular;

import Utilities.ByteUtil;
import Utilities.Constants;
import action.*;
import bus.BusDepot;
import connection.Sender;
import org.apache.commons.math3.exception.OutOfRangeException;
import xml.XML_ActionType;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * class that holds utility methods for the schedular
 */
public class SchedularUtil {

    /*-----------------------------------------------------------------------------------------------
      UTILITY METHODS FOR THE SCHEDULAR
      - processActionSequenceList
     ----------------------------------------------------------------------------------------------*/

    public static void processActionSequenceList(List<ActionSequence> actionSequenceList) {

        // check if ActionSequenceList is not empty
        if (!actionSequenceList.isEmpty()) {
            // not empty -> ActionSequence(s) have been triggered and need to be processed
            for (ActionSequence actionSequence : actionSequenceList) {
                // start processing the actionsequencee at the startIndex 0
                processActionSequence(actionSequence, 0);
            }
        }

    }


     /*-----------------------------------------------------------------------------------------------
      HELPER METHODS
      - processActionSequence
      - processAction
      - calculateByteValueByActionType
     ----------------------------------------------------------------------------------------------*/

    /**
     * note: this method needs to be public since the ActionMessageWaitRunnables call this method
     * @param actionSequence
     * @param startIndex
     */
    public static void processActionSequence(ActionSequence actionSequence, int startIndex) {

        // for each Action starting from the startIndex
        loop: for (int i = startIndex; i < actionSequence.getActionCount(); i++) {

            // get the action at the given index
            Action action = actionSequence.getAction(i);

            // determine which type of Action is present
            if (action instanceof ActionMessageBit) {
                // the action is a ActionMessageBit
                ActionMessageBit actionMessageBit = (ActionMessageBit) action;
                processActionMessage(actionMessageBit.getActionMessageBit(), XML_ActionType.BITMESSAGE);

            } else if (action instanceof ActionMessageBitToggle) {
                // the action is a ActionMessageBitToggle
                ActionMessageBitToggle actionMessageBitToggle = (ActionMessageBitToggle) action;
                processActionMessage(actionMessageBitToggle.getActionMessageBitToggle(), XML_ActionType.BITTOGGLE);

            } else if (action instanceof ActionMessageByte) {
                // the action is a ActionMessageByte
                ActionMessageByte actionMessageByte = (ActionMessageByte) action;
                processActionMessage(actionMessageByte.getActionMessageByte(), XML_ActionType.BYTEMESSAGE);

            } else if (action instanceof ActionMessageByteIncDecRement) {
                // the action is a ActionMessageByteIncDecRement
                ActionMessageByteIncDecRement actionMessageByteIncDecRement = (ActionMessageByteIncDecRement) action;
                processActionMessage(actionMessageByteIncDecRement.getActionMessageByteIncDecRement(), XML_ActionType.INCREMENT);

            } else if (action instanceof ActionWait) {
                // the action is a ActionMessageWait
                ActionWait actionWait = (ActionWait) action;
                processActionWait(actionWait.getWaitTime(), actionSequence, i+1);

                break loop; // the new thread continues to process the actionSequence starting from the startIndex
            }


        }

    }

    private static void processActionMessage(int[] actionArray, XML_ActionType actionType) {

        if(!BusDepot.getBusDepot().busExists(actionArray[0])) {
            // bus with the given id doesnt exist
            System.err.println("Bus with id " + actionArray[0] + " from rule does not exist!");
            return;
        }
        // bus exists


        // message for updating the server
        int[] rmxMessage = buildRMXMessage(actionArray, XML_ActionType.BITMESSAGE);

        // fake message so the changed bits by the action are getting checked in the matrix
        int[] fakeMessage = buildFakeMessage(actionArray, XML_ActionType.BITMESSAGE);

        // update the bus with changes of the fakeMessage
        // format of fakeMessage: format <0x99><BUS><SYSTEMADRESS><VALUE>
        BusDepot.getBusDepot().updateBus(fakeMessage[1], fakeMessage[2], fakeMessage[3]);

        // add RMXMessage to the sender for sending to the RMX-PC-Zentrale
        Sender.addMessageQueue(rmxMessage);

        // add fake message to the fakeMessageQueue so the changes are checked
        Schedular.getSchedular().addMessageToFakeQueue(fakeMessage);
    }

    private static void processActionWait(long waitTime, ActionSequence actionSequence, int startIndex) {

        if (startIndex == (actionSequence.getActionCount()-1)) {
            // the ActionWait is the last Action, no need to start a new Thread
            // -1 since the index starts at 0
        }
        // the ActionWait isn not the last action of the actionSequence

        // create ActionMessageWaitRunnable that continues to process the actionSequence starting from the startIndex
        ActionMessageWaitRunnable actionMessageWaitRunnable = new ActionMessageWaitRunnable(actionSequence, startIndex);

        // start a new thread that starts after the given waitTime
        Schedular.getSchedular().getExecutor().schedule(actionMessageWaitRunnable, waitTime, TimeUnit.MILLISECONDS);
    }


    /**
     * Builds an RMXMessage to be sent to the RMX-PC-Zentrale specified by the given actionArray, of the given ActionType
     * <p>
     * The given actionArray can have the format: <br>
     * - ActionMessageBit: [bus][systemadress][bitIndex][bitValue]<br>
     * - ActonMessageBitToggle: [bus][systemadress][bitIndex]<br>
     * - ActionMessageByte: [bus][systemadress][byteValue]<br>
     * - ActionMessageByteIncrement [bus][systemadress][value]<br>
     * - ActionMessageByteDecrement [bus][systemadress][ - value]<br>
     * <p>
     * (for the ActionMessageWait no FakeMessage is needed)
     *
     * @param actionArray int[] holding the data of the given action
     * @param actionType  the ActionType of the given action
     * @return fakeMessage as int[] with the format: <RMX-HEAD><COUNT><OPCODE><BUS><SYSTEMADRESS><VALUE>
     * @throws OutOfRangeException if the calculated byteValue by an ActionMessageIncrement or ActionMessageDecrement is
     *                             out of Range of a byte (greater than 255 or less than 0)
     */
    private static int[] buildRMXMessage(int[] actionArray, XML_ActionType actionType) throws OutOfRangeException {
        int[] message = new int[6];

        // RMX-Headbyte
        message[0] = Constants.RMX_HEAD;
        // COUNT
        message[1] = 6;
        // OPCODE
        message[2] = Constants.OPCODE_WRITE_TO_BUS_ADRESS_RMXMESSAGE; // 0x05 -> for writing a value to a bus adress
        // BUS
        message[3] = actionArray[0];
        // SYSTEMADRESS
        message[4] = actionArray[1];
        // VALUE - set byte to the calculated byteValue
        message[5] = calculateByteValueByActionType(actionArray, actionType);

        return message;
    }

    /**
     * Builds an FakeMessage for the schedular specified by the given actionArray, of the given ActionType
     * <p>
     * The given actionArray can have the format: <br>
     * - ActionMessageBit: [bus][systemadress][bitIndex][bitValue]<br>
     * - ActonMessageBitToggle: [bus][systemadress][bitIndex]<br>
     * - ActionMessageByte: [bus][systemadress][byteValue]<br>
     * - ActionMessageByteIncrement [bus][systemadress][value]<br>
     * - ActionMessageByteDecrement [bus][systemadress][ - value]<br>
     * <p>
     * (for the ActionMessageWait no FakeMessage is needed)
     *
     * @param actionArray int[] holding the data of the given action
     * @param actionType  the ActionType of the given action
     * @return fakeMessage as int[] with the format: <OPCODE><BUS><SYSTEMADRESS><VALUE>
     * @throws OutOfRangeException if the calculated byteValue by an ActionMessageIncrement or ActionMessageDecrement is
     *                             out of Range of a byte (greater than 255 or less than 0)
     */
    private static int[] buildFakeMessage(int[] actionArray, XML_ActionType actionType) throws OutOfRangeException {
        int[] message = new int[4];

        // OPCODE
        message[0] = Constants.OPCODE_WRITE_TO_BUS_ADRESS_FAKEMESSAGE; // 0x99
        // BUS
        message[1] = actionArray[0];
        // SYSTEMADRESS
        message[2] = actionArray[1];

        // VALUE - set byte to the calculated byteValue
        message[3] = calculateByteValueByActionType(actionArray, actionType);

        return message;
    }

    /**
     * Helper Method to calculate the calculates the new byteValue an FakeMessage and RMX-Message
     * specified by the given actionArray, of the given ActionType
     *
     * <p>
     * The given actionArray can have the format: <br>
     * - ActionMessageBit: [bus][systemadress][bitIndex][bitValue]<br>
     * - ActonMessageBitToggle: [bus][systemadress][bitIndex]<br>
     * - ActionMessageByte: [bus][systemadress][byteValue]<br>
     * - ActionMessageByteIncrement [bus][systemadress][value]<br>
     * - ActionMessageByteDecrement [bus][systemadress][ - value]<br>
     * <p>
     *
     * @param actionArray int[] holding the data of the given action
     * @param actionType  the ActionType of the given action
     * @return int the new byteValue that result of the action of the given ActionType
     * @throws OutOfRangeException if the calculated byteValue by an ActionMessageIncrement or ActionMessageDecrement is
     *                             out of Range of a byte (greater than 255 or less than 0)
     */
    private static int calculateByteValueByActionType(int[] actionArray, XML_ActionType actionType) throws OutOfRangeException {

        // newByteValue that is the result of the action of the given actionType
        int newByteValue = 0;

        // get the current value of the systemadress
        int currentByte = BusDepot.getBusDepot().getBus(actionArray[0]).getCurrentByte(actionArray[1]);

        // VALUE - determined by the given ActionType
        switch (actionType) {
            case BITMESSAGE:
                // action is a ActionMessageBit - only on bit needs to be set
                // array format: [bus][systemadress][bitIndex][bitValue]

                // set bit at position Bit to BitValue
                newByteValue = ByteUtil.setBitAtPos(currentByte, actionArray[2], actionArray[3]);
                break;

            case BITTOGGLE:
                // action is a ActionMessageBitToggle - only on bit needs to be toggled
                // array format: [bus][systemadress][bitIndex]

                // toggle bit at position BitIndex
                newByteValue = ByteUtil.toggleBitAtPos(currentByte, actionArray[2]);
                break;

            case BYTEMESSAGE:
                // action is a ActionMessageByte - whole byte value needs to be changed
                // array format: [bus][systemadress][byteValue]

                // set whole byte to the given byteValue
                newByteValue = actionArray[2];

                break;

            case DECREMENT:
            case INCREMENT:
                // action is a ActionMessageIncrement or ActionMessageDecrement - whole byte is incremented or decremented by the given Value
                // array format ActionMessageIncrement: [bus][systemadress][value]
                // array format ActionMessageIncrement: [bus][systemadress][-value]

                // calculate the new byteValue
                newByteValue = currentByte + actionArray[2];

                // if the new byteValue is Out of Range of a Byte not(0 <= newByteValue <= 255)
                if (newByteValue > 255 || newByteValue < 0) {
                    throw new OutOfRangeException(newByteValue, 0, 255);
                }
                break;
        } // end switch

        // return the new byteValue
        return newByteValue;
    }

}
