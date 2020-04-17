package schedular;


import bus.BusDepot;
import matrix.MatrixChecker;
import matrix.bitMatrix.BitMatrix;
import schedular.utilities.SchedularUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * class that represents the schedular responsible for processing incoming changes that need to be checked in
 * the matrix and processing the resulting triggered ActionSequences
 *
 */
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
     * the schedular runs in an own thread
     */
    private static Thread sThread;

    /**
     * indicates if initialization by the RMX-PC-Zetrale is completed
     * needs to be thread safe since multiple threads (Schedular and Receiver) check/set the constant
     */
    public static AtomicBoolean INIT_SUCESSFULL = new AtomicBoolean();

    /**
     * number of threads in the thread pool responsible for processing ActionWaits
     */
    public static int NUMBER_OF_THREADS = 5;

    /**
     * indicates if the SchedularThread should schedule, used for cleanup to stop the schedularThread
     */
    private boolean WORK = true;

    /**
     * Queue of fakeMessages
     * format: <0x99><BUS><SYSTEMADRESS><VALUE>
     * <p>
     * this queue has a higher priority than the RMXMessageQueue, so fake messages are checked at first
     * BlockingQueue to ensure thread safety.
     */
    private BlockingQueue<int[]> fakeMessageQueue;

    /**
     * Queue of rmxMessages
     * format: <0x06><BUS><SYSTEMADRESS><VALUE>
     * <p>
     * this queue stores incoming messages by the Receiver that need to be processed by the schedular.
     * BlockingQueue to ensure thread safety.
     */
    private BlockingQueue<int[]> rmxMessageQueue;

    /**
     * checker responsible for checking changes in the BitMatrix and ByteMatrix
     */
    MatrixChecker matrixChecker;

    /**
     * executor responsible for processing ActionWaits after the specified waitTime
     */
    private ScheduledExecutorService executor;

    /*-----------------------------------------------------------------------------------------------
      METHODS FOR SCHEDULING
      - startScheduling
      - addMessageToFakeQueue
      - addMessageToRMXQueue
      - getExecutor
      - cleanup
     ----------------------------------------------------------------------------------------------*/

    /**
     * initializes the scheduling of the schedular
     */
    public void startScheduling() {

        if (sThread == null) {
            // if no thread exists start a new one

            // queues - unbounded queues with no capacity restriction
            rmxMessageQueue = new LinkedBlockingQueue<>();
            fakeMessageQueue = new LinkedBlockingQueue<>();

            // thread
            sThread = new Thread(new SchedularThread());
            sThread.start();

            // executor
            executor = Executors.newScheduledThreadPool(NUMBER_OF_THREADS);

            // matrixChecker
            matrixChecker = MatrixChecker.getMatrixChecker();
        }
    }

    /**
     * Adds a message to the FakeMessageQueue of the Schedular
     * this queue only includes fake messages send by the Schedular itself or a SchedularWait-Thread
     * <p>
     * format: <0x99><BUS><SYSTEMADRESS><VALUE>
     *
     * @param message a message to add
     */
    public void addMessageToFakeQueue(int[] message) {
        fakeMessageQueue.add(message);
    }

    /**
     * Adds a message to the RMXMessageQueue of Schedular
     * this queue only includes messages send by the RMX-PC-Zentrale
     * <p>
     * format: <0x06><BUS><SYSTEMADRESS><VALUE>
     *
     * @param message a message to add
     */
    public void addMessageToRmxQueue(int[] message) {
        rmxMessageQueue.add(message);
    }

    /**
     * @return the ScheduledExecutorService of the schedular
     */
    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    /**
     * cleanup of the schedular
     * - stops the thread
     * - shuts down all threads of the executor
     * - empties the fakeMessageQueue and rmxMessageQueue
     */
    public void cleanup() {

        // stop the thread
        WORK = false;

        // cleanly shuts down all threads of the thread pool from the executor
        executor.shutdown();

        // empty the fakeMessageQueue and rmxMessageQueue
        fakeMessageQueue.clear();
        rmxMessageQueue.clear();
    }

    /*-----------------------------------------------------------------------------------------------
      SCHEDULAR-THREAD
     ----------------------------------------------------------------------------------------------*/

    /**
     * runnable for the schedular thread
     */
    private class SchedularThread implements Runnable {

        @Override
        public void run() {

            // wait until initialisation is done -> all used buses are initialised
            while (!INIT_SUCESSFULL.get()) {
                // do nothing
            }

            // initialisation done -> first check all conditions
            SchedularUtil.processActionSequenceList(matrixChecker.checkAllFields());

            // work until someone indicates i have to stop
            while (WORK) {

                while (fakeMessageQueue.isEmpty() && rmxMessageQueue.isEmpty()) {
                    // wait until one of the queues is not empty
                }

                Integer[] changes = new Integer[8];
                int[] message;

                // fakeMessage queue has a higher priority for checking
                if (!fakeMessageQueue.isEmpty()) {
                    // fake message(s) exist
                    message = fakeMessageQueue.poll(); // take message at first position

                    // format <0x99><BUS><SYSTEMADRESS><VALUE>
                    // bus has already been updated in SchedularUtil.processActionMessage()
                    changes = BusDepot.getBusDepot().getChanges(message[1], message[2]);
                } else {
                    // no fake messages exist -> take the next RMXMessage
                    message = rmxMessageQueue.poll(); // take message at first position

                    // format <0x06><BUS><SYSTEMADRESS><VALUE>
                    // also updates Bus
                    changes = BusDepot.getBusDepot().getChangesAndUpdate(message[1], message[2], message[3]);
                }

                // check fields of the matrix of the indicated changes in the changes Array
                // -1 -> no change, 0 -> change to 0, 1 -> change to 1
                // returns List of ActionSequences that have been triggered by the changes
                SchedularUtil.processActionSequenceList(matrixChecker.check(message[1], message[2], changes));
            }

        }
    }

}