package connection;

import connection.connectionutilities.OutputUtil;
import utilities.ByteUtil;
import utilities.Constants;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class responsible for sending messages
 */
public class Sender {
	/*
	 * synchronized list containing messages to be send to the RMX Server,
	 * operations / methods on the List should be declared synchronized to ensure
	 * maximum security regarding thread safety
	 */
	private static List<int[]> messageQueue = Collections.synchronizedList(new ArrayList<>());
	// Thread to send messages
	private static SenderThread senderThread;
	// output stream
	private static DataOutputStream outStream;

	// private Constructor to prevent initializing
	private Sender() {
	}

	/**
	 * Sends initializing Messages to RMX Server to establish connection Makes sure
	 * always Connection messages first when initializing
	 */
	protected static void initializeConnection() {
		if (senderThread == null) {
			addMessageAtIndex(0, Constants.POSITIVE_HANDSHAKE);
			addMessageAtIndex(1, Constants.LOKDATENBANK_MESSAGE);
			addMessageAtIndex(2, Constants.INITALIZATION_MESSAGE);
			senderThread = new SenderThread();
			senderThread.start();
		}
	}

	/**
	 * Addes a Message to messageQueue at a specific Index
	 *
	 * @param index   index to insert message
	 * @param message message to insert
	 */
	private synchronized static void addMessageAtIndex(int index, int[] message) {
		messageQueue.add(index, message);
	}

	/**
	 * appends a message at the end messageQueue
	 *
	 * @param message message to add
	 */
	public synchronized static void addMessageQueue(int[] message) {
		messageQueue.add(message);
	}

	/**
	 * Sends a message to the RMX Server
	 *
	 * @param bytes message to be send
	 * @throws IOException if error within DataOutputStream
	 */
	private static void sendMessage(int[] bytes) throws IOException {
		outStream = new DataOutputStream(SocketConnector.getSocket().getOutputStream());
		// write the message
		outStream.write(ByteUtil.convertIntArrayToByteArray(bytes));
	}

	/**
	 * checks if messageQueue is empty
	 *
	 * @return boolen if message queue is empty
	 */
	private synchronized static boolean isMessageQueueEmpty() {
		return messageQueue.isEmpty();
	}

	/**
	 * gets the first message in messagQueue
	 *
	 * @return first message in Message queue
	 * @throws ArrayIndexOutOfBoundsException if messageQueue is empty
	 */
	private synchronized static int[] getFirstMessage() throws ArrayIndexOutOfBoundsException {
		return messageQueue.remove(0);
	}

	/**
	 * Clear the Message Queue
	 */
	protected synchronized static void clearMessageQueue() {
		if (!isMessageQueueEmpty()) {
			messageQueue.clear();
		}
	}

	/**
	 * Method that sets the thread to null
	 */
	public synchronized static void setNull() {
		senderThread = null;
	}

	/**
	 * private Thread for sending messages to RMX Server
	 */
	private static class SenderThread extends Thread {
		public void run() {
			// loop until Connection is closed
			while (SocketConnector.getConStateStr().equals(SocketConnector.conState.RUNNING)) {
				/*
				 * only send message if the last command to the server is acknowledged by the
				 * Server
				 */
				if (!isMessageQueueEmpty() && SocketConnector.nextRequestAllowed.get()) {
					try {
						int[] message = getFirstMessage();
						sendMessage(message);
						// prevent sending of new message until server acknowledges last message
						SocketConnector.nextRequestAllowed.set(false);
						OutputUtil.writeMsgToConsole(message);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}