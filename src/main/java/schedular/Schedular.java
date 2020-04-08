package schedular;

import action.ActionWait;
import bus.BusDepot;
import Utilities.Constants;
import connection.Sender;
import matrix.Matrix;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import Utilities.ByteUtil;
import action.Action;
import action.ActionMessage;
import action.ActionSequence;

public class Schedular {

    /**
     * number of threads in the thread pool that are responsible for processing ActionWaits
     */
    public static int NUMBER_OF_THREADS = 5;

    /**
     * indicates if initialization is done
     * needs to be thread safe since multiple threads check the constant
     */
    public static AtomicBoolean INIT_SUCESSFULL = new AtomicBoolean();

    /**
     * Queue of fake messages (OPCODE 0x99)
     * this queue has a higher priority than the messageQueue, so fake messages are checks at first
     */
    private BlockingQueue<byte[]> fakeMessageQueue;

    /**
     * Queue to store incoming received messages by the Receiver that need to be
     * processed by the schedular. BlockingQueue to ensure thread safety.
     * <p>
     * ONLY INCLUDES 0x06 messages
     */
    private BlockingQueue<byte[]> rmxMessageQueue;


    /**
     * executer responsible for scheduling actions after a specific delay
     */
    private ScheduledExecutorService executer;

    /**
     * Provided Matrix (=Tabelle) the schedular works with to determine which
     * action sequence(s) are triggered for the messages
     */
    private volatile Matrix matrix;

    private volatile BusDepot busDepot;

    // Singleton-Pattern START -----------------------------------------

    /**
     * Singleton instance of schedular
     */
    private static Schedular schedularInstance;

    /**
     * object for schedular thread. Used for handling all schedular tasks
     */
    private static Thread sThread;

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
            matrix = Matrix.getMatrix();
        }
    }

    public void cleanup() {

        // TODO thread beenden
        executer.shutdown(); // cleanly shutsdown all threads of the threadpool (wenns nicht gemacht wird laufen die
        // threads aus dem Pool einfach unendlich lange weiter und Programm beendet nie
        // TODO ggf. Liste leeren

    }

    /**
     * Addsa  message to the RMX Queue of schedular
     * this queue only includes messages send by the RMX-PC-Zentrale
     *
     * @param message a message that needs to be added
     */
    public void addMessageToRmxQueue(byte[] message) {
        // TODO possibly only allow when queue not null?
        rmxMessageQueue.add(message);
    }

    /**
     * Adds a message to the Fake Queue of the schedular
     * this queue only includes fake messages send by the schedular or a SchedularWait-Thread
     *
     * @param message a message that needs to be added
     */
    public void addMessageToFakeQueue(byte[] message) {
        fakeMessageQueue.add(message);
    }


    /**
     * Schedular Thread.
     */
    private class SchedularThread implements Runnable {

        @Override
        public void run() {

            // wait until inititialization is successfull --> all busses are filled
            while (!INIT_SUCESSFULL.get()) {
                // do nothing
            }

            // init sucessfull --> firstly check all conditions
            checkAllConditions(); // TODO

            // TODO add useful termination condition for thread
            while (true) {

                // changes has only more than one bit set to one if multiple bits are set at the SAME time
                Integer[] changes = new Integer[8];
                byte[] message;

                // TODO evtl. überlegen mit wait und notify
                while (fakeMessageQueue.isEmpty() && rmxMessageQueue.isEmpty()) {
                    // wait until one queue isnt emtpty
                }

                if (!fakeMessageQueue.isEmpty()) {
                    // fake message(s) exist
                    message = fakeMessageQueue.poll(); // take message at first position
                    changes = busDepot.getChanges(message); // bus has already been updated
                } else {
                    // no fake messages exist
                    message = rmxMessageQueue.poll();
                    changes = busDepot.getChangesAndUpdate(message); // also updates Bus
                }

                // Example Value 1 from RMX-1 Adress 98 Value 1 <0x06><0x01><0x62><0x01>
                System.out.println("ICH WERDE JETZT PRÜFEN");


                System.out.println("LAST CHANGES: " + Arrays.toString(changes));

               List<ActionSequence> actionSequenceList = matrix.check(message[1], message[2], changes);

                // check if list is not empty
                if (!actionSequenceList.isEmpty()) {
                    for (ActionSequence actionSequence : actionSequenceList) {
                        // start processing the actionsequenc at the given startIndex
                        scheduleActionSequence(actionSequence, 0);
                    }
                }

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
        for (int i = startIndex; i < actionSequence.getActionCount(); i++) {

            // get the action at the given index
            Action action = actionSequence.getAction(i);

            if (action instanceof ActionMessage) {
                // the action is a ActionMessage
                ActionMessage actionMessage = (ActionMessage) action;
                System.out.println("------ACTION " + Arrays.toString(actionMessage.getActionMesssage()));

                // actionArr with the action message
                int[] actionArr = actionMessage.getActionMesssage();

                // need to check if bus exists otherwise the connection will be killed by the RMX-PC-Zentrale
                if (busDepot.busExists(actionArr[0])) {

                    // message for updating the server
                    byte[] message = buildRmxMessage(actionMessage.getActionMesssage());

                    // fake message so the changed bits by the action are getting checked in the matrix
                    byte[] fakeMessage = buildFakeMessage(actionMessage.getActionMesssage());

                    // Update bus
                    busDepot.updateBus(fakeMessage);

                    // add (real) message to the sender
                    Sender.addMessageQueue(message);

                    // add fake message to the fakeMessageQueue
                    addMessageToFakeQueue(fakeMessage);

                } else {
                    // bus isnt initialized
                    System.out.println("-> Adress bus " + actionArr[0] + " from rule does not exist!");
                }
            } else {
                // the Action is a ActionWait

                // if the ActionWait is last action in the ActionSequence no need to start a new Thread
                if ((i + 1) < actionSequence.getActionCount()) {

                    ActionWait actionWait = (ActionWait) action;

                    // get the wait time
                    long time = actionWait.getWaitTime();

                    // start new thread that continious to process the next action of the actionsequence after the given delay
                    SchedularWaitRunnable waitRunnable = new SchedularWaitRunnable(actionSequence, i + 1);
                    executer.schedule(waitRunnable, time, TimeUnit.MILLISECONDS);

                    break; // know the new thread continious to process the actionSequence starting from the startIndex
                }
            }

        }
    }

    public void checkAllConditions() {

        List<ActionSequence> actionSequenceList = matrix.checkAllFields();

        // check if list is not empty
        if (!actionSequenceList.isEmpty()) {
            for (ActionSequence actionSequence : actionSequenceList) {
                // start processing the actionsequencee at the given startIndex
                scheduleActionSequence(actionSequence, 0);
            }
        }

    }

    /**
     * Method that converts the int Array of the action to a message
     * builds an Fake Message (OPCODE 0x99)
     * Necessary because change needs to be checked, but status has already been updateed
     * <p>
     * [BUS](1-4) [SystemAddresse](0-111) [Bit](0-7) [BitValue] (0-1) format
     * <0x99><RMX><ADRRMX><VALUE>
     *
     * @param intArr array containing the Bus, Systemadresse, Bit, Bitvalue of the given Action
     * @return a fake Message in RMXnet Syntax
     */
    private byte[] buildFakeMessage(int[] intArr) {
        byte[] message = new byte[4];
        // Opcode
        message[0] = (byte) 153; //0x99
        // bus <rmx>
        message[1] = (byte) intArr[0];
        // systemadresse <addrRMX>
        message[2] = (byte) intArr[1];
        // value
        Byte currentbyte = busDepot.getBus(intArr[0]).getCurrentByte(intArr[1]);
        message[3] = (byte) ByteUtil.setBitAtPos(currentbyte, intArr[2], intArr[3]);

        return message;
    }

    /**
     * Method that converts the int Array of the action to a RMX Message
     *
     * [BUS](1-4) [SystemAddresse](0-111) [Bit](0-7) [BitValue] (0-1) format
     * <0x06><RMX><ADRRMX><VALUE>
     *
     * @param intArr array containing the Bus, Systemadresse, Bit, Bitvalue of the given Action
     * @return a Message in RMXnet Syntax
     */
    private byte[] buildRmxMessage(int[] intArr) {
        byte[] message = new byte[6];
        // headbyte
        message[0] = Constants.RMX_HEAD;
        // Count
        message[1] = 6;
        // Opcode
        message[2] = 5;
        // bus <rmx>
        message[3] = (byte) intArr[0];
        // <addrRMX>
        message[4] = (byte) intArr[1];
        // value
        Byte currentbyte = busDepot.getBus(intArr[0]).getCurrentByte(intArr[1]);
        message[5] = (byte) ByteUtil.setBitAtPos(currentbyte, intArr[2], intArr[3]);

        return message;
    }

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
        public void run() {
            scheduleActionSequence(actionSequence, startIndex);
        }

    }

}