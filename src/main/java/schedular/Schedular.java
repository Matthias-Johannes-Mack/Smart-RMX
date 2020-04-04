package schedular;

import bus.BusDepot;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Schedular {

    // indicates if initialization is done
    // needs to be thread safe since multiple threads check the constant
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


    private ScheduledExecutorService executer;

    /**
     * Provided RuleSet (=Tabelle) the schedular works with which determines which actions are triggered for the messages
     */
    //TODO private RuleSet ruleSet;

    public void startScheduling() {
        // if no thread exists start a new one
        if (sThread == null) {

            // init queue
            messageQueue = new LinkedBlockingQueue<>(); // unbounded queue with no capacity restriction

            // create Thread
            sThread = new Thread(new SchedularThread());
            sThread.start();

            // create Executor
            executer = Executors.newSingleThreadScheduledExecutor(); // only one Thread possibly more --> THREADPOOL

        }
    }

    public void cleanup() {

        //TODO thread beenden
        executer.shutdown();
        //TODO ggf. Liste leeren

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

            // wait until inititialization is sucessfull --> all busses are filled
            while (!INIT_SUCESSFULL.get()){
                // do nothing
            }

            // init sucessfull --> firstly check all conditions
            //TODO checkAllConditions --> nur über lastChanged aller Adressen kann sich so arbeits sparen

            // TODO add useful termination condition for thread
            while (true) {
                try {
                    // if queue is empty, the thread blocks (!no active waiting) and waits for an message to become available
                    byte[] message = messageQueue.take();

                    byte changes = BusDepot.getBusDepot().getChanges(message);
                    // convert to BitSet
                    BitSet bits = BitSet.valueOf(new byte[] {changes});

                    // loop only iterates only set bits
                    // 0 = bit hasnt changed
                    // 1 = bit has changed => condition needs to be checked
                    for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i+1)) {
                        //TODO check Zustand an Position "Bernd´s Matrix": Bus + Systemadresse*8 + i
                        // i entspricht gesetzer bit position
                        // return dann ggf. auzuführende Actions in Form etwa Action1, Wait(zeit), Action2,...


                        // EXAMPLE
                        // Evtl. verbung mit Action und Kinder Message und Wait

                        List<Integer> actions = new ArrayList<>();

                        /*
                        for all actions

                        if(type == action) {
                            lege action auf Sender
                        } else
                        type == wait

                        neue Timertask mit Time = Waitdauer und übergebe Rest der ActionListe an Timer

                        break; aus loop raus => arbeit wird an timertask übergeben
                        }
                         */
                        SchedularTimerTask task = new SchedularTimerTask(actions);
                        executer.schedule(task, 5, TimeUnit.SECONDS); // starts task after 5 seconds


                    }

                    // does nothing if no bit has changed

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private class SchedularTimerTask implements Runnable {

        List<Integer> actions = new ArrayList<>();

        public SchedularTimerTask(List<Integer> actions) {
            this.actions = actions;
        }

        @Override
        public void run() {

        /*
                 for all actions

                if(type == action) {
                    lege action auf Sender
                } else
                    type == wait

                    neue Timertask mit Time = Waitdauer und übergebe Rest der ActionListe an Timer
                    break; aus loop raus => arbeit wird an timertask übergeben
                }

                */
        }

    }



}
