package schedular.schedularutilities;

import action.actions.*;
import action.actionSequence.ActionSequence;
import action.actions.actionMessages.ActionMessageBit;
import action.actions.actionMessages.ActionMessageBitToggle;
import action.actions.actionMessages.ActionMessageByte;
import action.actions.actionMessages.ActionMessageByteIncDecRement;
import schedular.Schedular;
import utilities.ByteUtil;
import utilities.Constants;
import bus.BusDepot;
import connection.Sender;
import utilities.ActionType;

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

    /**
     * processes each ActionSequence of the given actionSequenceList
     *
     * @param actionSequenceList a actionSequenceList to process
     */
    public static void processActionSequenceList(List<ActionSequence> actionSequenceList) {

        if (!actionSequenceList.isEmpty()) {
            // not empty -> ActionSequence(s) have been triggered and need to be processed
            for (ActionSequence actionSequence : actionSequenceList) {
                // start processing the actionSequence at the startIndex 0
                processActionSequence(actionSequence, 0);
            }
        }

    }

     /*-----------------------------------------------------------------------------------------------
      HELPER METHODS
      - processActionSequence
      - processActionMessage
      - processActionWait

      HELPER METHODS FOR MESSAGES
      - buildRMXMessage
      - buildFakeMessage
      - calculateByteValueByActionType
     ----------------------------------------------------------------------------------------------*/

    /**
     * processes a actionSequence by processing its actions starting from the given startIndex
     * <p>
     * note: this method needs to be public since the ActionWaitRunnable also calls this method
     *
     * @param actionSequence a actionSequence to process
     * @param startIndex     the index of the action of the actionSequence to start processing from
     */
    public static void processActionSequence(ActionSequence actionSequence, int startIndex) {

        // for each action starting from the given startIndex
        loop:
        for (int i = startIndex; i < actionSequence.getActionCount(); i++) {

            // get the action at the given index
            Action action = actionSequence.getAction(i);

            // determine which type of Action is present and process accordingly
            if (action instanceof ActionMessageBit) {
                // the action is a ActionMessageBit
                ActionMessageBit actionMessageBit = (ActionMessageBit) action;
                processActionMessage(actionMessageBit.getActionMessageBit(), ActionType.BITMESSAGE);

            } else if (action instanceof ActionMessageBitToggle) {
                // the action is a ActionMessageBitToggle
                ActionMessageBitToggle actionMessageBitToggle = (ActionMessageBitToggle) action;
                processActionMessage(actionMessageBitToggle.getActionMessageBitToggle(), ActionType.BITTOGGLE);

            } else if (action instanceof ActionMessageByte) {
                // the action is a ActionMessageByte
                ActionMessageByte actionMessageByte = (ActionMessageByte) action;
                processActionMessage(actionMessageByte.getActionMessageByte(), ActionType.BYTEMESSAGE);

            } else if (action instanceof ActionMessageByteIncDecRement) {
                // the action is a ActionMessageByteIncDecRement
                ActionMessageByteIncDecRement actionMessageByteIncDecRement = (ActionMessageByteIncDecRement) action;
                processActionMessage(actionMessageByteIncDecRement.getActionMessageByteIncDecRement(), ActionType.INCREMENT);

            } else if (action instanceof ActionWait) {
                // the action is a ActionMessageWait
                ActionWait actionWait = (ActionWait) action;
                processActionWait(actionWait.getWaitTime(), actionSequence, i + 1);

                break loop; // the new thread continues to process the actionSequence starting from the startIndex
            }

        } // end loop

    }

    /**
     * processes a action specified by the given actionArray according to the given actionType
     * <p>
     * resposible for processing all ActionsTypes beside ActionWait:
     * - ActionMessageBit<br>
     * - ActionMessageBitToggle<br>
     * - ActionMessageByte<br>
     * - ActionMessageByteIncDecRement<br>
     *
     * @param actionArray int[] holding the data of the given action
     * @param actionType  the ActionType of the given action
     */
    private static void processActionMessage(int[] actionArray, ActionType actionType) {

        if (!BusDepot.getBusDepot().busExists(actionArray[0])) {
            // bus with the given id doesnt exist
            System.out.println("Bus with id " + actionArray[0] + " from rule does not exist!");
            return;
        }
        // bus exists

        int[] rmxMessage;
        int[] fakeMessage;

        try {
            // message for sending to the RMX-PC-Zentrale
            rmxMessage = buildRMXMessage(actionArray, actionType);

            // message for the schedular so the changes are also getting checked in the matrix
            fakeMessage = buildFakeMessage(actionArray, actionType);

        } catch (IndexOutOfBoundsException e) {
            // is thrown if the new byteValue in the calculation of an ActionMessageIncDecRement
            // is Out of Range of a Byte not(0 <= newByteValue <= 255)
            System.out.println(e.getMessage());
            return;
        }

        // update the bus with the fakeMessage (to update lastChanges of the given bus and systemadress)
        // format of fakeMessage: format <0x99><BUS><SYSTEMADRESS><VALUE>
        BusDepot.getBusDepot().updateBus(fakeMessage[1], fakeMessage[2], fakeMessage[3]);

        // add RMXMessage to the sender for sending to the RMX-PC-Zentrale
        Sender.addMessageQueue(rmxMessage);

        // add fake message to the fakeMessageQueue of the schedular
        Schedular.getSchedular().addMessageToFakeQueue(fakeMessage);
    }

    /**
     * processes a ActionWait specified by the given waitTime and the actionSequence that needs to be continued
     * to be processed after the given waitTime, starting from the given startIndex.
     *
     * @param waitTime       the waitTime of the ActionWait
     * @param actionSequence the actionSequence to be continued to be processed after the waitTime
     * @param startIndex     the startIndex to start processing the given actionSequence after the given waitTime
     */
    private static void processActionWait(long waitTime, ActionSequence actionSequence, int startIndex) {

        // index of the initial ActionMessageWait = startIndex - 1
        // index of last action in the ActionSequence = actionCount - 1
        if ((startIndex - 1) == (actionSequence.getActionCount() - 1)) {
            // the ActionWait is the last Action, no need to start a new Thread
            // -1 since the index starts at 0
            return;
        }
        // the ActionWait is not the last action of the actionSequence

        // create ActionWaitRunnable that continues to process the actionSequence starting from the startIndex
        ActionWaitRunnable actionMessageWaitRunnable = new ActionWaitRunnable(actionSequence, startIndex);

        // start a new thread that starts after the given waitTime
        Schedular.getSchedular().getExecutor().schedule(actionMessageWaitRunnable, waitTime, TimeUnit.MILLISECONDS);
    }


    /*-----------------------------------------------------------------------------------------------
      HELPER METHODS FOR MESSAGES
      - buildRMXMessage
      - buildFakeMessage
      - calculateByteValueByActionType
     ----------------------------------------------------------------------------------------------*/

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
     * @throws IndexOutOfBoundsException if the calculated byteValue by an ActionMessageIncrement or ActionMessageDecrement is
     *                             out of Range of a byte (greater than 255 or less than 0)
     */
    private static int[] buildRMXMessage(int[] actionArray, ActionType actionType) throws IndexOutOfBoundsException {
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
     * @throws IndexOutOfBoundsException if the calculated byteValue by an ActionMessageIncrement or ActionMessageDecrement is
     *                             out of Range of a byte (greater than 255 or less than 0)
     */
    private static int[] buildFakeMessage(int[] actionArray, ActionType actionType) throws IndexOutOfBoundsException {
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
     * @throws IndexOutOfBoundsException if the calculated byteValue by an ActionMessageIncrement or ActionMessageDecrement is
     *                             out of Range of a byte (greater than 255 or less than 0)
     */
    private static int calculateByteValueByActionType(int[] actionArray, ActionType actionType) throws IndexOutOfBoundsException {

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

                // toggle bit at position bitIndex
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
                    throw new IndexOutOfBoundsException("Increment / Decrement is out of Range of an Byte: " +  newByteValue);
                }

                break;

        } // end switch

        // return the new byteValue
        return newByteValue;
    }

}
