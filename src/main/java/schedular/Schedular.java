package schedular;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Schedular {

    // indicates if initialization is done
    public static AtomicBoolean INIT_SUCESSFULL = new AtomicBoolean();

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
     * Queue to store incoming received messages by the Receiver that need to be processed by the schedular.
     * BlockingQueue to ensure thread safety.
     *
     * ONLY INCLUDES 0x06 messages
     */
    private BlockingQueue<byte[]> messageQueue;

    /**
     * Provided RuleSet the schedular works with which determines which actions are triggered for the messages
     */
    //TODO private RuleSetDummy ruleSet;

    public void startScheduling() {
        // if no thread exists start a new one
        if (sThread == null) {

            // init queue
            messageQueue = new LinkedBlockingQueue<>(); // unbounded queue with no capacity restriction

            // create Thread
            sThread = new Thread(new SchedularThread());
            sThread.start();
        }
    }

    /**
     * Adds message to queue of schedular
     *
     * @param message a message that needs to be processed
     */
    public void addMessage(byte[] message) {
        //TODO possibly only allow when queue not null?
        messageQueue.add(message);
    }

    /**
     * Schedular Thread.
     */
    private class SchedularThread implements Runnable {

        @Override
        public void run() {

            // wait until inititialization is sucessfull
            while (!INIT_SUCESSFULL.get()){
                // do nothing
            }

            // init sucessfull
            //TODO checkAllTrains -> Aufruf auf RuleSet


            // TODO add useful termination condition for thread
            while (true) {
                try {
                    // if queue is empty, the thread blocks (!no active waiting) and waits for an message to become available
                    byte[] message = messageQueue.take();

                    //Integer[] trainState = TrainDepot.getTrainDepot().getTrainState(message);

                    /*
                    TODO
                    1. Zustand an RuleSet / Tabelle senden
                    2. Falls actions als antwort (null wenn keine?) zum Sender hinzufügen
                     */

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }



}
