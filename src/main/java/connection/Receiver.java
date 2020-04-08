package connection;

import Utilities.ByteUtil;
import Utilities.Constants;
import bus.BusDepot;
import schedular.Schedular;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class responsible for receiving messages from the server
 * 
 * @author Jan Dammrath
 * 
 */
class Receiver {

	// Singleton-Pattern START -----------------------------------------

	/**
	 * Singleton instance of Receiver
	 */
	private static Receiver receiverInstance;

	/**
	 * private constructor to prevent instantiation
	 */
	private Receiver() {

	}

	/**
	 * Returns singleton Receiver instance
	 *
	 * @return Receiver Singleton instance
	 */
	public static synchronized Receiver getReceiver() {
		if (receiverInstance == null) {
			receiverInstance = new Receiver();

		}
		return receiverInstance;
	}

	// Singleton-Pattern END ________________________________________________

	/**
	 * Thread for sending the messages
	 */
	private ReceiverThread receiverThread;

	/**
	 * InputStream for the messages to the RMX-PC-Zentrale
	 */
	private InputStream inputStr;

	/**
	 * Starts the receiver Thread and sets up the InputStream to the RMX-PC-Zentrale
	 */
	public void startReceiver() {
		// if the socketConnector is requesting a new thread, do it
		if (receiverThread == null) {
			try {
				inputStr = SocketConnector.getSocketConnector().getSocket().getInputStream();
			} catch (Exception e) {
				e.printStackTrace();
			}
			receiverThread = new ReceiverThread();
			receiverThread.start();
		}
	}

	/**
	 * Receives Message from server and returns thee message without the RMXnet Headbyte
	 * (HEAD) and Number of Bytes (COUNT)
	 *
	 * RMXnet message format: <HEAD><COUNT><OPCODE><DATA>
	 *
	 * @return byte[] - message
	 */
	private byte[] receive() throws IOException {

		int msgLength;

		// read Headybyte
		msgLength = inputStr.read();

		// skip Headbyte and read messageSize
		while (msgLength == Constants.RMX_HEAD) {
			// Second byte of message always represents the messageSize (inclusive Headbyte)
			msgLength = inputStr.read();
		}

		// initialize message array (ignores Headbyte and Messagelength => -2)
		byte[] message = new byte[msgLength - 2];

		// receive actual message
		for (int i = 0; i < message.length; i++) {
			message[i] = (byte) inputStr.read();
		}

		return message;
	}


	/**
	 * Method that sets the thread to null
	 */
	public synchronized void setNull() {
		receiverThread = null;
	}

	/*-----------------------------------------------------------------------------------------------
	  MESSAGE OPCODE RECOGNITION
     ----------------------------------------------------------------------------------------------*/

	/**
	 * Processes message (without HEAD and COUNT) in dependence of the OPCODE. Sets
	 * nextRequestAllowed in SocketConnecter to true, if message is an
	 * acknoledgement.
	 *
	 * RMXnet OPCODE (receiving): - 0 (0x00) positive acknowledgement - 1 (0x01)
	 * negative acknowledgement - 3 (0x03) initialisation response - 4 (0x04) state
	 * info - 6 (0x06) rmx-adress value (RMX-1 Bus) - 8 (0x08) lok-info (adress,
	 * type, name) - 32 (0x20) rmx-channel info (RMX-0 Bus) - 36 (0x24) lok-info
	 * speed and direction - 40 (0x28) lok-info functions (f0 - f16) - 192 (0xc0)
	 * read lok-decoder
	 *
	 * @param message a message to process without HEAD and COUNT
	 */
	public void processMessage(byte[] message) {

		int opcode = -1;

		if (message.length > 0) {

			// first byte in message is OPCODE
			opcode = message[0];

			switch (opcode) {
			case 0: // 0x00 - positive acknowledgement

				if (message[1] == 0) {
					// "final" positive acknowledgement
					SocketConnector.getSocketConnector().nextRequestAllowed.set(true);
				}

				// else message[1] == 0x01: positive acknowledgment "Bearbeitung läuft"

				break;
			case 1: // 0x01 - negative acknowledgement
				process0x01(message);
				SocketConnector.getSocketConnector().nextRequestAllowed.set(true);
				break;
			case 3: // 0x03 - initialisation response
				// TODO Wenn nicht gleiche RMX Version terminieren

				SocketConnector.getSocketConnector().nextRequestAllowed.set(true);
				break;
			case 4: // 0x04 - state info
				process0x04(message);
				SocketConnector.getSocketConnector().nextRequestAllowed.set(true);
				// put in the current system time
				ServerReload.setLastServerResponse(System.currentTimeMillis());
				break;
			case 6: // 0x06 - rmx-adress value (RMX-1 Bus)
				process0x06(message);
				break;
			case 8: // 0x06 - lok-info (adress, type, name)
				process0x08(message);
				break;
			case 32: // 0x20 - rmx-channel info (RMX-0 Bus)
				process0x20(message);
				break;
			case 36: // 0x24 - lok-info speed and direction
				process0x24(message);
				SocketConnector.getSocketConnector().nextRequestAllowed.set(true);
				break;
			case 40: // 0x28 - lok-info functions (f0 - f16)
				process0x28(message);
				SocketConnector.getSocketConnector().nextRequestAllowed.set(true);
				break;
			case 192: // 0x0c0 read lok-decoder
				break;
			default:
				System.out.println("Message received with unknown OPCODE");
				break;
			}
		}
	}

	/**
	 * processsing a negative acknowledgment OPCODE 0x01. Prints error message on
	 * console. format <0x01><0x0?>
	 *
	 * @param message a message to process
	 */
	private void process0x01(byte[] message) {

		switch (message[1]) {
		case 1: // 0x01 - unknown OPCODE
			System.err.println("negative acknowledgment: unknown OPCODE");
			break;
		case 3: // 0x03 - lok not in database
			System.err.println("negative acknowledgment: lok not in database");
			break;
		case 4: // 0x04 - input error
			System.err.println("negative acknowledgment: input error");
			break;
		case 5: // 0x05 mode unequal to 0x01
			System.err.println("negative acknowledgment: mode unequal to 0x01");
			break;
		case 7: // 0x07 - lok database full
			System.err.println("negative acknowledgment: lok database full");
			break;
		case 8: // 0x08 - control channels occupied (RMX-0 Bus)
			System.err.println("negative acknowledgment: control channels occupied");
			break;
		}
	}

	/**
	 * processsing state info OPCODE 0x04 format <0x04><STATUS>
	 *
	 * @param message a message to process
	 */
	private void process0x04(byte[] message) {

		if (Schedular.INIT_SUCESSFULL.get() == false) {
			// check initialization
			byte status = message[1];

			// checks if bit 5 und 6 is set in Status => initialisation sucessfull
			if (ByteUtil.bitIsSet(status, 5) && ByteUtil.bitIsSet(status, 6)) {
				System.out.println("----INIT SUCESFULLL----");

				Schedular.INIT_SUCESSFULL.set(true);
			}
		}
	}

	/**
	 * processsing rmx-adress value (RMX-1 Bus) OPCODE 0x06. Forwards message to
	 * schedular
	 *
	 * Example Value 1 from RMX-1 Adress 98 Value 1 <0x06><0x01><0x62><0x01>
	 *
	 * @param message a message to process
	 */
	private void process0x06(byte[] message) {

		if (Schedular.INIT_SUCESSFULL.get()) { // true -- init successfull

			// forward message to schedular
			Schedular.getSchedular().addMessageToRmxQueue(message);
			System.out.println("Message zu Schedular hinzugefuegt!");

		} else { // false -- init not successfull

			// updates bus -> creates if not existing
			BusDepot.getBusDepot().updateBus(message[1], message[2],message[3]);
		}

	}

	/*
		unused for know
	 */

	private void process0x08(byte[] message) {

	}

	private void process0x20(byte[] message) {

	}

	private void process0x24(byte[] message) {

	}

	private void process0x28(byte[] message) {

	}

	/*-----------------------------------------------------------------------------------------------
	  RECEIVER-THREAD
     ----------------------------------------------------------------------------------------------*/

	/**
	 * Thread for receiving messages from the RMX-PC-Zentrale
	 */
	private class ReceiverThread extends Thread {
		public void run() {
			// Loop while Connection is established
			while (SocketConnector.getSocketConnector().getConStateStr().equals(SocketConnector.conState.RUNNING)) {
				// wait for next message

				try {
					// while there is a message loop
					while (inputStr.available() > 0) {
						byte[] msg = receive();
						// write all messages to console
						OutputUtil.writeMsgToConsole(msg);
						// switch the opt codes
						processMessage(msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}