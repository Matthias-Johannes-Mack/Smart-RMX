package connection;

import Utilities.Constants;
import schedular.Schedular;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class responsible for sending messages
 */
public class Sender {

	// Singleton-Pattern START -----------------------------------------

	/**
	 * Singleton instance of Sender
	 */
	private static Sender senderInstance;

	/**
	 * private constructor to prevent instantiation
	 */
	private Sender() {

	}

	/**
	 * Returns singleton Sender instance
	 *
	 * @return Sender Singleton instance
	 */
	public static synchronized Sender getSender() {
		if (senderInstance == null) {
			senderInstance = new Sender();

		}
		return senderInstance;
	}

	// Singleton-Pattern END ________________________________________________

	/**
	 * Thread for sending the messages
	 */
	private static SenderThread senderThread;

	/**
	 * Outputstream for the messages to the RMX-PC-Zentrale
	 */
	private static DataOutputStream outStream;

	/**
	 * synchronized list containing messages to be send to the RMX-PC-Zentrale
	 * synchronized to guarantee thread safty
	 */
	private List<byte[]> messageQueue;


	/**
	 * Sends initializing messages to RMX-PC-Zentrale to establish connection makes sure
	 * always Connection messages first when initializing
	 *
	 * process to initialize the connection (defined by RMXnet protocol)
	 * 1. send positive handshake
	 * 2. get the lok data
	 * 3. send the initialization message
	 *
	 */
	protected void initializeConnection() {
		if (senderThread == null) {

			// initialize messageQueue
			senderInstance.messageQueue = Collections.synchronizedList(new ArrayList<>());

			// add initialization procedure
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
	private synchronized  void addMessageAtIndex(int index, byte[] message) {
		messageQueue.add(index, message);
	}

	/**
	 * appends a message at the end messageQueue
	 * 
	 * @param message message to add
	 */
	public synchronized void addMessageQueue(byte[] message) {
		messageQueue.add(message);
	}

	/**
	 * Sends a message to the RMX Server
	 * 
	 * @param bytes message to be send
	 * @throws IOException if error within DataOutputStream
	 */
	private void sendMessage(byte[] bytes) throws IOException {
		outStream = new DataOutputStream(SocketConnector.getSocket().getOutputStream());
		// write the message to the RMX-PC-Zentrale
		outStream.write(bytes);
	}

	/**
	 * checks if the messageQueue is empty
	 * 
	 * @return boolen if message queue is empty
	 */
	private synchronized boolean isMessageQueueEmpty() {
		return messageQueue.isEmpty();
	}

	/**
	 * gets the first message in messagQueue
	 * 
	 * @return first message in Message queue
	 * @throws ArrayIndexOutOfBoundsException if messageQueue is empty
	 */
	private synchronized byte[] getFirstMessage() throws ArrayIndexOutOfBoundsException {
		return messageQueue.remove(0);
	}

	/**
	 * Clears the messageQueue of the Sender
	 */
	protected synchronized void clearMessageQueue() {
		if (!isMessageQueueEmpty()) {
			messageQueue.clear();
		}
	}

	/**
	 * Method that sets the thread to null
	 */
	public synchronized void setNull() {
		senderThread = null;
	}

	/**
	 * Thread for sending messages to the RMX-PC-Zentrale
	 */
	private class SenderThread extends Thread {
		public void run() {
			// loop until Connection is closed
			while (SocketConnector.getConStateStr().equals(SocketConnector.conState.RUNNING)) {
				/*
				 * only send message if the last command to the server is acknowledged by the
				 * Server
				 */
				if (!isMessageQueueEmpty() && SocketConnector.nextRequestAllowed.get()) {
					try {
						byte[] message = getFirstMessage();
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
