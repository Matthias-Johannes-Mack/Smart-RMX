package schedular;

import action.*;
import bus.BusDepot;
import Utilities.Constants;
import connection.Sender;
import matrix.BitMatrix;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import Utilities.ByteUtil;
import org.apache.commons.math3.exception.OutOfRangeException;

public class Schedular {

    // Singleton-Pattern START -----------------------------------------

    /**
     * Singleton instance of schedular
     */
    private static Schedular schedularInstance;

    /**
     * private constructor to prevent instantiation
     */
    private Schedular() {

    }

    /**
     * Returns singleton schedular instance
     *
     * @return Schedular Singleton instance
     */
    public static synchronized Schedular getSchedular() {
        if (schedularInstance == null) {
            schedularInstance = new Schedular();
        }
        return schedularInstance;
    }

    // Singleton-Pattern END ________________________________________________

    /**
     * object for schedular thread. Used for handling all schedular tasks
     */
    private static Thread sThread;

    /**
     * indicates if the SchdularThread should schedule messages, used for cleanup to stop the schedularThread
     */
    private boolean WORK = true;

    /**
     * number of threads in the thread pool that are responsible for processing ActionWaits
     */
    public static int NUMBER_OF_THREADS = 5;

    /**
     * indicates if initialization by the RMX-PC-Zetrale is completed
     * needs to be thread safe since multiple threads (Schedular and Receiver) check the constant
     */
    public static AtomicBoolean INIT_SUCESSFULL = new AtomicBoolean();

    /**
     * Queue of fake messages (OPCODE 0x99)
     * this queue has a higher priority than the RMXMessageQueue, so fake messages are checked at first
     * BlockingQueue to ensure thread safety.
     */
    private BlockingQueue<int[]> fakeMessageQueue;

    /**
     * Queue of fake messages (OPCODE 0x06)
     * this queue stores incoming messages by the Receiver that need to be processed by the schedular.
     * BlockingQueue to ensure thread safety.
     */
    private BlockingQueue<int[]> rmxMessageQueue;


    /**
     * executer responsible for scheduling ActionWait(s) after the specified waitTime
     */
    private ScheduledExecutorService executer;

    /**
     * Provided Matrix (=Tabelle) the schedular works with to determine which
     * ActionSequence(s) are triggered for the changes of the messages
     */
    private volatile BitMatrix matrix;

    /**
     * busDepot handling the access to the different busses
     */
    private volatile BusDepot busDepot;


    /*-----------------------------------------------------------------------------------------------
      METHODS FOR SCHEDULING
      - startScheduling
      - addMessageToFakeQueue
      - addMessageToRMXQueue
      - cleanup
     ----------------------------------------------------------------------------------------------*/

    /**
     * initializes all variables for the schedular
     */
    public void startScheduling() {
        // if no thread exists start a new one

        if (sThread == null) {

            // init queues
            rmxMessageQueue = new LinkedBlockingQueue<>(); // unbounded queue with no capacity restriction
            fakeMessageQueue = new LinkedBlockingQueue<>();

            // create Thread
            sThread = new Thread(new SchedularThread());
            sThread.start();

            // create Executor
            executer = Executors.newScheduledThreadPool(NUMBER_OF_THREADS);

            // busdepot
            busDepot = BusDepot.getBusDepot();

            // matrix
            matrix = BitMatrix.getMatrix();
        }
    }

    /**
     * Adds a message to the FakeMessageQueue of the Schedular
     * this queue only includes fake messages send by the Schedular itself or a SchedularWait-Thread
     *
     * @param message a message to add
     */
    public void addMessageToFakeQueue(int[] message) {
        fakeMessageQueue.add(message);
    }

    /**
     * Adds message to the RMXMessageQueue of Schedular
     * this queue only includes messages send by the RMX-PC-Zentrale
     *
     * @param message a message to add
     */
    public void addMessageToRmxQueue(int[] message) {
        rmxMessageQueue.add(message);
    }

    /**
     * method to initialiaze the cleanup of the schedular
     */
    public void cleanup() {

        // stop the thread
        WORK = false;
        // cleanly shutsdown all threads of the threadpool
        executer.shutdown();
        // TODO ggf. Liste leeren

    }

    /*-----------------------------------------------------------------------------------------------
      SCHEDULAR-THREAD
     ----------------------------------------------------------------------------------------------*/

    /**
     * Schedular Thread.
     */
    private class SchedularThread implements Runnable {

        @Override
        public void run() {

            // wait until inititialization is successfull => all busses are filled
            while (!INIT_SUCESSFULL.get()) {
                // do nothing
            }

            // init sucessfull => first check all conditions
            checkAllConditions();

            // do until someone indicates I have to stop
            while (WORK) {

                Integer[] changes = new Integer[8];
                int[] message;

                while (fakeMessageQueue.isEmpty() && rmxMessageQueue.isEmpty()) {
                    // wait until one queue isnt emtpty
                }

                // fakeMessage queue has a higher priority for checking
                if (!fakeMessageQueue.isEmpty()) {
                    // fake message(s) exist
                    message = fakeMessageQueue.poll(); // take message at first position
                    // format <0x99><RMX><ADRRMX><VALUE>
                    changes = busDepot.getChanges(message[1], message[2]); // bus has already been updated
                } else {
                    // no fake messages exist
                    message = rmxMessageQueue.poll();
                    // format <0x06><RMX><ADRRMX><VALUE>
                    changes = busDepot.getChangesAndUpdate(message[1], message[2], message[3]); // also updates Bus
                }


                // check fields of the matrix that included indicated changes and get triggered ActionSequences
               List<ActionSequence> actionSequenceList = matrix.check(message[1], message[2], changes);

                // check if ActionSequence(s) have been triggerd
                if (!actionSequenceList.isEmpty()) {
                    for (ActionSequence actionSequence : actionSequenceList) {
                        // start processing the actionsequencee at the startIndex 0
                        scheduleActionSequence(actionSequence, 0);
                    }
                }

            }

        }
    }

    /*-----------------------------------------------------------------------------------------------
      HELPER METHODS FOR SCHEDULING
      - checkAllConditions
      - scheduleActionSequence
      - buildFakeMessage
      - buildRMXMessage
     ----------------------------------------------------------------------------------------------*/

    /**
     * checks all conditions by traversing the entire matrix
     */
    public void checkAllConditions() {

        // check all fields of the matrix and get triggered ActionSequences
        List<ActionSequence> actionSequenceList = matrix.checkAllFields();

        // check if ActionSequence(s) have been triggerd
        if (!actionSequenceList.isEmpty()) {
            for (ActionSequence actionSequence : actionSequenceList) {
                // start processing the actionsequencee at the startIndex 0
                scheduleActionSequence(actionSequence, 0);
            }
        }

    }

    /**
     * Method processing the given Actions in the given ActionSequence starting from the given startIndex
     * Creates a new Thread that continious to process the ActionSequence if an ActionWait occurs.
     *
     * @param actionSequence a ActionSequenz to process
     * @param startIndex     index at which the processing of the Actions starts
     */
    private void scheduleActionSequence(ActionSequence actionSequence, int startIndex) {

        // for each Action starting from the startIndex
        loop: for (int i = startIndex; i < actionSequence.getActionCount(); i++) {

            // get the action at the given index
            Action action = actionSequence.getAction(i);

            if (action instanceof ActionMessageBit) {
                // the action is a ActionMessage
                ActionMessageBit actionMessageBit = (ActionMessageBit) action;
                System.out.println("------ACTION " + Arrays.toString(actionMessageBit.getActionMessageBit()));

                // actionArr with the action message
                int[] actionArr = actionMessageBit.getActionMessageBit();

                // need to check if bus exists otherwise the connection will be killed by the RMX-PC-Zentrale
                if (busDepot.busExists(actionArr[0])) {

                    // message for updating the server
                    int[] message = buildRmxMessageBit(actionMessageBit.getActionMessageBit());

                    // fake message so the changed bits by the action are getting checked in the matrix
                    int[] fakeMessage = buildFakeMessageBit(actionMessageBit.getActionMessageBit());

                    // update bus
                    // format <0x99><RMX><ADRRMX><VALUE>
                    busDepot.updateBus(fakeMessage[1], fakeMessage[2], fakeMessage[3]);

                    // add (real) message to the sender for sending to the RMX-PC-Zentrale
                    Sender.addMessageQueue(message);

                    // add fake message to the fakeMessageQueue so the changes are checked
                    addMessageToFakeQueue(fakeMessage);

                } else {
                    // the has not been initialized
                    System.out.println("-> Adress bus " + actionArr[0] + " from rule does not exist!");
                }
            } else if (action instanceof ActionWait) {
                // the Action is a ActionWait

                // if the ActionWait is last action in the ActionSequence no need to start a new Thread, just skip it
                if ((i + 1) < actionSequence.getActionCount()) {
                    // the ActiionWait isnt the last action

                    ActionWait actionWait = (ActionWait) action;

                    // get the wait time
                    long time = actionWait.getWaitTime();

                    // start new thread that continious to process the next action of the ActionSequence after the given waitTime
                    SchedularWaitRunnable waitRunnable = new SchedularWaitRunnable(actionSequence, i + 1);
                    executer.schedule(waitRunnable, time, TimeUnit.MILLISECONDS);

                    break; // know the new thread continious to process the actionSequence starting from the startIndex, i can go on to the next ActionSequence
                }
            } else if (action instanceof ActionMessageByte) {
                // the action is a ActionMessageByte
                ActionMessageByte actionMessageByte = (ActionMessageByte) action;
                System.out.println("------ACTION-Byte " + Arrays.toString(actionMessageByte.getActionMessageByte()));

                // actionArr with the action message
                int[] actionArr = actionMessageByte.getActionMessageByte();

                // need to check if bus exists otherwise the connection will be killed by the RMX-PC-Zentrale
                if (busDepot.busExists(actionArr[0])) {

                    // message for updating the server
                    int[] message = buildRmxMessageByte(actionMessageByte.getActionMessageByte());

                    // fake message so the changed bits by the action are getting checked in the matrix
                    int[] fakeMessage = buildFakeMessageByte(actionMessageByte.getActionMessageByte());

                    // update bus
                    // format <0x99><RMX><ADRRMX><VALUE>
                    busDepot.updateBus(fakeMessage[1], fakeMessage[2], fakeMessage[3]);

                    // add (real) message to the sender for sending to the RMX-PC-Zentrale
                    Sender.addMessageQueue(message);

                    // add fake message to the fakeMessageQueue so the changes are checked
                    addMessageToFakeQueue(fakeMessage);

                } else {
                    // the has not been initialized
                    System.out.println("-> Adress bus " + actionArr[0] + " from rule does not exist!");
                }
            } else if (action instanceof ActionMessageByteIncDecRement) {
                // the action is a ActionMessageByteIncrement
                ActionMessageByteIncDecRement actionMessageByteIncDecRement = (ActionMessageByteIncDecRement) action;
                System.out.println("Schedular------ACTION-Byte--INCREMENT " + actionMessageByteIncDecRement.getActionMessageByteIncDecRement());

                // actionArr with the action message
                int[] actionArr = actionMessageByteIncDecRement.getActionMessageByteIncDecRement();

                System.err.println("Schedular actionArr ActionMessageByteIncDec: " + Arrays.toString(actionArr));

                // need to check if bus exists otherwise the connection will be killed by the RMX-PC-Zentrale
                if (busDepot.busExists(actionArr[0])) {

                    int[] message = null;
                    int[] fakeMessage = null;
                    try {
                        // message for updating the server
                        message = buildRmxMessageByteInDecRement(actionArr);

                        // fake message so the changed bits by the action are getting checked in the matrix
                        fakeMessage = buildFakeMessageByteIncDecRement(actionArr);
                    } catch(OutOfRangeException e) {
                        System.err.println("Catch OutofRangeException");
                        e.printStackTrace();
                        break loop;
                    }


                    // update bus
                    // format <0x99><RMX><ADRRMX><VALUE>
                    busDepot.updateBus(fakeMessage[1], fakeMessage[2], fakeMessage[3]);

                    // add (real) message to the sender for sending to the RMX-PC-Zentrale
                    Sender.addMessageQueue(message);

                    // add fake message to the fakeMessageQueue so the changes are checked
                    addMessageToFakeQueue(fakeMessage);

                } else {
                    // the has not been initialized
                    System.out.println("-> Adress bus " + actionArr[0] + " from rule does not exist!");
                }
            }  else if (action instanceof ActionMessageBitToggle) {
                // the action is a ActionMessageBitToggle
                ActionMessageBitToggle actionMessageBitToggle = (ActionMessageBitToggle) action;
                System.out.println("------ACTION-Byte " + Arrays.toString(actionMessageBitToggle.getActionMessageBitToggle()));

                // actionArr with the action message
                int[] actionArr = actionMessageBitToggle.getActionMessageBitToggle();

                // need to check if bus exists otherwise the connection will be killed by the RMX-PC-Zentrale
                if (busDepot.busExists(actionArr[0])) {

                    // message for updating the server
                    int[] message = buildRmxMessageBitToggle(actionMessageBitToggle.getActionMessageBitToggle());

                    // fake message so the changed bits by the action are getting checked in the matrix
                    int[] fakeMessage = buildFakeMessageBitToggle(actionMessageBitToggle.getActionMessageBitToggle());

                    // update bus
                    // format <0x99><RMX><ADRRMX><VALUE>
                    busDepot.updateBus(fakeMessage[1], fakeMessage[2], fakeMessage[3]);

                    // add (real) message to the sender for sending to the RMX-PC-Zentrale
                    Sender.addMessageQueue(message);

                    // add fake message to the fakeMessageQueue so the changes are checked
                    addMessageToFakeQueue(fakeMessage);

                } else {
                    // the has not been initialized
                    System.out.println("-> Adress bus " + actionArr[0] + " from rule does not exist!");
                }
            }
        }
    }

    /**
     * Method that converts the int Array of an ActionMessage to a FakeMessage (OPCODE 0x99)
     * Necessary because change needs to be checked, but status has already been updateed
     *
     * Format:
     *  OPCODE [busId](1-4) [systemAdress](0-111) [bitIndex](0-7) [bitValue] (0-1) format
     * <0x99>  <RMX><ADRRMX><VALUE>
     *
     * @param intArr array containing the busId, systemadress, bitIndex, bitvalue of the given Action
     * @return a fake Message in RMXnet Syntax
     */
    private int[] buildFakeMessageBit(int[] intArr) {
        int[] message = new int[4];
        // OPCODE
        message[0] = 153; //0x99
        // bus <rmx>
        message[1] = intArr[0];
        // systemadress <addrRMX>
        message[2] = intArr[1];
        // value
        int currentbyte = busDepot.getBus(intArr[0]).getCurrentByte(intArr[1]);
        message[3] = ByteUtil.setBitAtPos(currentbyte, intArr[2], intArr[3]);

        return message;
    }

    /**
     * Method that converts the int Array of the ActionMessage to a RMXnet Message
     *
     * OPCODE [busId](1-4) [systemAdress](0-111) [bitIndex](0-7) [bitValue] (0-1) format
     * <0x06>  <RMX><ADRRMX><VALUE>
     *
     * @param intArr array containing the busId, systemadress, bitIndex, bitValue of the given Action
     * @return a Message in RMXnet Syntax
     */
    private int[] buildRmxMessageBit(int[] intArr) {
        int[] message = new int[6];
        // RMX-Headbyte
        message[0] = Constants.RMX_HEAD;
        // COUNT
        message[1] = 6;
        // OPCODE
        message[2] = 5;
        // bus <rmx>
        message[3] = intArr[0];
        // systemadress <addrRMX>
        message[4] = intArr[1];
        // value
        int currentbyte = busDepot.getBus(intArr[0]).getCurrentByte(intArr[1]);
        message[5] = ByteUtil.setBitAtPos(currentbyte, intArr[2], intArr[3]);

        return message;
    }

    /**
     * Method that converts the int Array of an ActionMessageBitToggle to a FakeMessage (OPCODE 0x99)
     * Necessary because change needs to be checked, but status has already been updateed
     *
     * Format:
     *  OPCODE [busId](1-4) [systemAdress](0-111) [bitIndex](0-7)
     * <0x99>  <RMX><ADRRMX><VALUE>
     *
     *
     * @param intArr array containing the busId, systemadress, bit to toggle
     * @return a fake Message in RMXnet Syntax
     */
    private int[] buildFakeMessageBitToggle(int[] intArr) {
        int[] message = new int[4];
        // OPCODE
        message[0] = 153; //0x99
        // bus <rmx>
        message[1] = intArr[0];
        // systemadress <addrRMX>
        message[2] = intArr[1];
        // value
        int currentbyte = busDepot.getBus(intArr[0]).getCurrentByte(intArr[1]);
        message[3] = ByteUtil.toggleBitAtPos(currentbyte, intArr[2]);

        return message;
    }

    /**
     * Method that converts the int Array of the ActionMessage to a RMXnet Message
     *
     * OPCODE [busId](1-4) [systemAdress](0-111) [bitIndex](0-7)
     * <0x06>  <RMX><ADRRMX><VALUE>
     *
     * @param intArr array containing the busId, systemadress, bitIndex of the given Action
     * @return a Message in RMXnet Syntax
     */
    private int[] buildRmxMessageBitToggle(int[] intArr) {
        int[] message = new int[6];
        // RMX-Headbyte
        message[0] = Constants.RMX_HEAD;
        // COUNT
        message[1] = 6;
        // OPCODE
        message[2] = 5;
        // bus <rmx>
        message[3] = intArr[0];
        // systemadress <addrRMX>
        message[4] = intArr[1];
        // value
        int currentbyte = busDepot.getBus(intArr[0]).getCurrentByte(intArr[1]);
        message[5] = ByteUtil.toggleBitAtPos(currentbyte, intArr[2]);

        return message;
    }

    /**
     * Method that converts the int Array of an ActionMessageByte to a FakeMessage (OPCODE 0x99)
     * Necessary because change needs to be checked, but status has already been updateed
     *
     * Format:
     *  OPCODE [busId][systemAdress][byteValue]
     * <0x99>  <RMX><ADRRMX><VALUE>
     *
     * @param intArr array containing the busId, systemadress, byteValue of the given Action
     * @return a fake Message in RMXnet Syntax
     */
    private int[] buildFakeMessageByte(int[] intArr) {

        int[] message = new int[4];
        // OPCODE
        message[0] = 153; //0x99
        // bus <rmx>
        message[1] = intArr[0];
        // systemadress <addrRMX>
        message[2] = intArr[1];
        // value
        message[3]= intArr[2];

        return message;
    }

    /**
     * Method that converts the int Array of the ActionMessageByte to a RMXnet Message
     *
     * OPCODE  [busId][systemAdress][byteValue
     * <0x06>  <RMX><ADRRMX><VALUE>
     *
     * @param intArr array containing the busId, systemadress, byteValue
     * @return a Message in RMXnet Syntax
     */
    private int[] buildRmxMessageByte(int[] intArr) {
        int[] message = new int[6];
        // RMX-Headbyte
        message[0] = Constants.RMX_HEAD;
        // COUNT
        message[1] = 6;
        // OPCODE
        message[2] = 5;
        // bus <rmx>
        message[3] = intArr[0];
        // systemadress <addrRMX>
        message[4] = intArr[1];
        // value
        message[5] = intArr[2];

        return message;
    }


    /**
     * Method that converts the int Array of an ActionMessageIncDec to a FakeMessage (OPCODE 0x99)
     * Necessary because change needs to be checked, but status has already been updateed
     *
     * Format:
     *  OPCODE [busId][systemAdress][incDecRementValue]
     * <0x99>  <RMX><ADRRMX><VALUE>
     *
     * @param  incrementValue array containing the busId, systemadress, incDecRementValue of the given Action
     * @return a fake Message in RMXnet Syntax
     * @throws OutOfRangeException if the incremented or decremented value is out of bounds (not in 0 <= x <= 255)
     */
    private int[] buildFakeMessageByteIncDecRement(int[] incrementValue) throws OutOfRangeException {

        int[] message = new int[4];
        // OPCODE
        message[0] = 153; //0x99
        // bus <rmx>
        message[1] = incrementValue[0];
        // systemadress <addrRMX>
        message[2] = incrementValue[1];
        // value
        int currentbyte = busDepot.getBus(incrementValue[0]).getCurrentByte(incrementValue[1]);

        int newByteValue = currentbyte + incrementValue[2];
        if(newByteValue > 255 || newByteValue < 0) {
            throw new OutOfRangeException(newByteValue, 0, 255);
        }
        message[3] = newByteValue;

        return message;
    }

    /**
     * Method that converts the int Array of the ActionMessageIncDec to a RMXnet Message
     *
     * OPCODE  [busId][systemAdress][byteValue
     * <0x06>  <RMX><ADRRMX><VALUE>
     *
     * @param  incrementValue array containing the busId, systemadress, incDecRementValue of the given Action
     * @return a fake Message in RMXnet Syntax
     * @throws OutOfRangeException if the incremented or decremented value is out of bounds (not in 0 <= x <= 255)
     */
    private int[] buildRmxMessageByteInDecRement(int[] incrementValue) {
        int[] message = new int[6];
        // RMX-Headbyte
        message[0] = Constants.RMX_HEAD;
        // COUNT
        message[1] = 6;
        // OPCODE
        message[2] = 5;
        // bus <rmx>
        message[3] = incrementValue[0];
        // systemadress <addrRMX>
        message[4] = incrementValue[1];
        // value
        int currentbyte = busDepot.getBus(incrementValue[0]).getCurrentByte(incrementValue[1]);

        System.out.println("Schedular buildRMXMessageByteIncDec - currentbyte: " + currentbyte);

        int newByteValue = currentbyte + incrementValue[2];
        if(newByteValue > 255 || newByteValue < 0) {
            throw new OutOfRangeException(newByteValue, 0, 255);
        }

        System.out.println("Schedular buildRMXMessageByteIncDec - newByteValue: " + newByteValue);

        message[5] = newByteValue;

        return message;
    }

     /*-----------------------------------------------------------------------------------------------
      HELPER CLASS FOR PROCESSING ACTIONWAIT
     ----------------------------------------------------------------------------------------------*/

    /**
     * Class that handles the processing of an ActionWait
     *
     * Continuous ot process the given ActionSequence starting from the given startIndex
     */
    private class SchedularWaitRunnable implements Runnable {

        ActionSequence actionSequence;

        int startIndex;

        public SchedularWaitRunnable(ActionSequence actionSequence, int startIndex) {
            this.actionSequence = actionSequence;
            this.startIndex = startIndex;
        }

        @Override
        /**
         * after the given delay at initialization of the Runnable the run method will be executed
         */
        public void run() {
            scheduleActionSequence(actionSequence, startIndex);
        }

    }

}