package schedular;

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

	private static Matrix matrix;

	private static BusDepot busDepot;

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
			busDepot = BusDepot.getBusDepot();
		}
		return schedularInstance;
	}

	// Singleton-Pattern END ________________________________________________

	/**
	 * Queue to store incoming received messages by the Receiver that need to be
	 * processed by the schedular. BlockingQueue to ensure thread safety.
	 *
	 * ONLY INCLUDES 0x06 messages
	 */
	private BlockingQueue<byte[]> messageQueue;

	private ScheduledExecutorService executer;

	/**
	 * Provided RuleSet (=Tabelle) the schedular works with which determines which
	 * actions are triggered for the messages
	 */
	// TODO private RuleSet ruleSet;

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

		// TODO thread beenden
		executer.shutdown();
		// TODO ggf. Liste leeren

	}

	/**
	 * Adds message to queue of schedular
	 *
	 * @param message a message that needs to be processed
	 */
	public void addMessage(byte[] message) {
		// TODO possibly only allow when queue not null?
		messageQueue.add(message);
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
				try {
					// if queue is empty, the thread blocks (!no active waiting) and waits for an
					// message to become available
					byte[] message = messageQueue.take();

					byte changes = BusDepot.getBusDepot().getChanges(message);

					/**
					 * Example Value 1 from RMX-1 Adress 98 Value 1 <0x06><0x01><0x62><0x01>
					 **/
					List<ActionSequence> actions = matrix.check(message[1], message[2], changes);
					// check if list is not empty
					if (!actions.isEmpty()) {
						for (ActionSequence as : actions) {
							// call the method action sequence
							scheduleActionSequence(as);
						}
					}


				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * Method for each ActionSequence
	 */
	private void scheduleActionSequence(ActionSequence actionSequence) {
		for (Action a : actionSequence.getActions()) {
			if (a instanceof ActionMessage) {
				Sender.addMessageQueue(buildRmxMessage(((ActionMessage) a).getActionMesssage()));
			} else {
				// TODO wait Message!
//				/*
//				 * for all actions if(type == action) { lege action auf Sender } else type ==
//				 * wait neue Timertask mit Time = Waitdauer und übergebe Rest der ActionListe
//				 * an Timer break; aus loop raus => arbeit wird an timertask übergeben }
//				 */
////				SchedularTimerTask task = new SchedularTimerTask(actions);
//				executer.schedule(task, 5, TimeUnit.SECONDS); // starts task after 5 seconds
//
//				// does nothing if no bit has changed
			}
		}
	}

	/**
	 * Method that converts the int Array of the action to a message
	 * 
	 * [BUS](1-4) [SystemAddresse](0-111) [Bit](0-7) [BitValue] (0-1)
	 * 
	 * @param intArr
	 * @return
	 */
	private byte[] buildRmxMessage(int[] intArr) {
		byte[] messageArr = new byte[6];
		// headbyte
		messageArr[0] = ConnectionConstants.RMX_HEAD;
		// Count
		messageArr[1] = 6;
		// Opcode
		messageArr[2] = 5;
		// bus <rmx>
		messageArr[3] = (byte) intArr[0];
		// <addrRMX>
		messageArr[4] = (byte) intArr[1];
		// value
		Byte currentbyte = busDepot.getBus(intArr[0]).getCurrentByte(intArr[1]);
		messageArr[5] = (byte) ByteUtil.setBitAtPos(currentbyte, intArr[2], intArr[3]);
		return messageArr;
	}

	private class SchedularTimerTask implements Runnable {

		List<Integer> actions = new ArrayList<>();

		public SchedularTimerTask(List<Integer> actions) {
			this.actions = actions;
		}

		@Override
		public void run() {

			/*
			 * for all actions if(type == action) { lege action auf Sender } else type ==
			 * wait neue Timertask mit Time = Waitdauer und übergebe Rest der ActionListe
			 * an Timer break; aus loop raus => arbeit wird an timertask übergeben }
			 */
		}

	}

}