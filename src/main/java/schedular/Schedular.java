package schedular;

import action.ActionWait;
import bus.BusDepot;
import connection.ConnectionConstants;
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
    private static Matrix matrix;

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
            matrix = Matrix.getMatrix();
        }
        return schedularInstance;
    }

    // Singleton-Pattern END ________________________________________________

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
        }
    }

    public void cleanup() {

        // TODO thread beenden
        executer.shutdown(); // cleanly shutsdown all threads of the threadpool (wenns nicht gemacht wird laufen die
        // threads aus dem Pool einfach unendlich lange weiter und Programm beendet nie
        // TODO ggf. Liste leeren

    }

    /**
     * Adds message to queue of schedular
     *
     * @param message a message that needs to be processed
     */
    public void addMessageToRmxQueue(byte[] message) {
        // TODO possibly only allow when queue not null?
        rmxMessageQueue.add(message);
    }

    /**
     * Adds message to fake queue of the schedular
     *
     * @param message a message that needs to be processed
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

            // wait until inititialization is sucessfull --> all busses are filled
            while (!INIT_SUCESSFULL.get()) {
                // do nothing
            }

            // init sucessfull --> firstly check all conditions
            matrix.checkAllConditions();

            // TODO add useful termination condition for thread
            while (true) {

                // changes has only more than one bit set to one if multiple bits are set at the SAME time
                byte changes = 0;
                byte[] message;

                // TODO evtl. überlegen mit wait und notify
                while (fakeMessageQueue.isEmpty() && rmxMessageQueue.isEmpty()) {
                    // wait until one queue isnt emtpty
                }

                if (!fakeMessageQueue.isEmpty()) {
                    // fake message(s) exist
                    System.out.println("Das ist Fake News");
                    message = fakeMessageQueue.poll(); // take message at first position
                    changes = busDepot.getChanges(message);

                } else {
                    // no fake messages exist
                    System.out.println("ICH BIN VOR TAKE");
                    message = rmxMessageQueue.poll();
                    System.out.println("Das ist eine richtige Nachricht");
                    changes = busDepot.getChangesAndUpdate(message); // also updates Bus!!!
                }

                /**
                 * Example Value 1 from RMX-1 Adress 98 Value 1 <0x06><0x01><0x62><0x01>
                 **/
                System.out.println("ICH WERDE JETZT PRÜFEN");
                List<ActionSequence> actionSequenceList = matrix.check(message[1], message[2], changes);

                // check if list is not empty
                if (!actionSequenceList.isEmpty()) {
                    for (ActionSequence actionSequence : actionSequenceList) {
                        // call the method action sequence
                        scheduleActionSequence(actionSequence, 0);
                    }
                }

            }

        }
    }

    /**
     * Method for each ActionSequence
     */
    private void scheduleActionSequence(ActionSequence actionSequence, int startIndex) {

        for (int i = startIndex; i < actionSequence.getActionCount(); i++) {

            Action action = actionSequence.getAction(i);

            if (action instanceof ActionMessage) {
                ActionMessage actionMessage = (ActionMessage) action;
                // actionArr with the action message
                int[] actionArr = actionMessage.getActionMesssage();
                // need to check if bus exists otherwise the connection will be killed
                if (busDepot.busExists(actionArr[0])) {

                    // message for updating the server
                    byte[] message = buildResponse(actionMessage.getActionMesssage());

                    // fake message to myself so we can determine if a message is a real (0x06) or fake(0x99) message
                    byte[] fakeMessage = buildRmx0x99Message(actionMessage.getActionMesssage());

                    // UPDATE to ensure if multiple changes happend (changes more than one value set to 1) are
                    // checked as a whole byte (der Erste bit berücksichtigt beim check auch Änderungen der nacholgenden bits)
                    busDepot.updateBus(fakeMessage);

                    Sender.addMessageQueue(message);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    addMessageToFakeQueue(fakeMessage);

                } else {
                    System.out.println("-> Adress bus " + actionArr[0] + " from rule does not exist!");
                }
            } else {

                // if wait action is last action no need to start a new Thread
                if ((i + 1) < actionSequence.getActionCount()) {

                    // message is a ActionWait
                    ActionWait actionWait = (ActionWait) action;

                    // get wait time
                    long time = actionWait.getWaitTime();

                    // start new thread that continious to process the next action of the actionsequence after the given delay
                    SchedularWaitRunnable waitRunnable = new SchedularWaitRunnable(actionSequence, i + 1);
                    executer.schedule(waitRunnable, time, TimeUnit.MILLISECONDS);

                    break; // know the new thread continious to process the actionSequence
                }
            }

        }
    }

    /**
     * Method that converts the int Array of the action to a message
     * builds an artificial change message (OPCODE 0x99)
     * Necessary because change needs to be checked, but status has already been updateed
     * <p>
     * [BUS](1-4) [SystemAddresse](0-111) [Bit](0-7) [BitValue] (0-1) format
     * <0x06><RMX><ADRRMX><VALUE>
     *
     * @param intArr
     * @return
     */
    private byte[] buildRmx0x99Message(int[] intArr) {
        byte[] message = new byte[4];
        // Opcode
        message[0] = (byte) 153; //0x99
        // bus <rmx>
        message[1] = (byte) intArr[0];
        // <addrRMX>
        message[2] = (byte) intArr[1];
        // value
        Byte currentbyte = busDepot.getBus(intArr[0]).getCurrentByte(intArr[1]);

        message[3] = (byte) ByteUtil.setBitAtPos(currentbyte, intArr[2], intArr[3]);

        return message;
    }

    /**
     * Method that converts the int Array of the action to a message
     * <p>
     * [BUS](1-4) [SystemAddresse](0-111) [Bit](0-7) [BitValue] (0-1)
     *
     * @param intArr
     * @return
     */
    private byte[] buildResponse(int[] intArr) {
        byte[] message = new byte[6];
        // headbyte
        message[0] = ConnectionConstants.RMX_HEAD;
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

    private class SchedularWaitRunnable implements Runnable {

        ActionSequence actionSequence;

        int startIndex;

        public SchedularWaitRunnable(ActionSequence actionSequence, int startIndex) {
            this.actionSequence = actionSequence;
            this.startIndex = startIndex;
        }

        @Override
        public void run() {
            System.out.println("EIN THREAD WURDE GESTARTET MIT: " + startIndex);
            scheduleActionSequence(actionSequence, startIndex);
        }

    }

}