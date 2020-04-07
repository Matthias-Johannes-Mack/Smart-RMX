package connection;

import Utilities.ByteUtil;
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
	private static InputStream inputStr;
	private static ReceiverThread receiverThread;

	/**
	 * private Constructor to prevent instantiation
	 */
	private Receiver() {
	}

	/**
	 * Starts the receiver Thread and ets uop the inputstream
	 */
	protected static void startReceiver() {
		// if the socketConnector is requesting a new thread, do it
		if (receiverThread == null) {
			try {
				inputStr = SocketConnector.getSocket().getInputStream();
			} catch (Exception e) {
				e.printStackTrace();
			}
			receiverThread = new ReceiverThread();
			receiverThread.start();
		}
	}

	/**
	 * Method that sets the thread to null
	 */
	public synchronized static void setNull() {
		receiverThread = null;
	}

	/**
	 * private Thread responsible for receiving messages from the Server
	 */
	private static class ReceiverThread extends Thread {
		public void run() {
			// Loop while Connection is established
			while (SocketConnector.getConStateStr().equals(SocketConnector.conState.RUNNING)) {
				// wait for next message
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
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

	// ======================================================================
	// --------------- Implementierung mit Nachrichtenerkennung -------------

	/**
	 * Receives Message from server and returns message without the RMXnet Headbyte
	 * (HEAD) and Number of Bytes (COUNT)
	 * <p>
	 * RMXnet message format: HEAD-COUNT-OPCODE-DATA
	 *
	 * @return byte[] - message
	 */
	private static byte[] receive() throws IOException {

		int msgLength;

		// read Headybyte
		msgLength = inputStr.read();

		// skip Headbyte and read messageSize
		while (msgLength == ConnectionConstants.RMX_HEAD) {
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
	 * Processes message (without HEAD and COUNT) in dependence of the OPCODE. Sets
	 * nextRequestAllowed in SocketConnecter to true, if message is an
	 * acknoledgement.
	 * <p>
	 * RMXnet OPCODE (receiving): - 0 (0x00) positive acknowledgement - 1 (0x01)
	 * negative acknowledgement - 3 (0x03) initialisation response - 4 (0x04) state
	 * info - 6 (0x06) rmx-adress value (RMX-1 Bus) - 8 (0x08) lok-info (adress,
	 * type, name) - 32 (0x20) rmx-channel info (RMX-0 Bus) - 36 (0x24) lok-info
	 * speed and direction - 40 (0x28) lok-info functions (f0 - f16) - 192 (0xc0)
	 * read lok-decoder
	 *
	 * @param message a message to process without HEAD and COUNT
	 */
	public static void processMessage(byte[] message) {

		int opcode = -1;

		if (message.length > 0) {

			// first byte in message is OPCODE
			opcode = message[0];

			switch (opcode) {
			case 0: // 0x00 - positive acknowledgement

				if (message[1] == 0) {
					// "final" positive acknowledgement
					SocketConnector.nextRequestAllowed.set(true);
				}

				// else message[1] == 0x01: positive acknowledgment "Bearbeitung l√§uft"

				break;
			case 1: // 0x01 - negative acknowledgement
				process0x01(message);
				SocketConnector.nextRequestAllowed.set(true);
				break;
			case 3: // 0x03 - initialisation response
				// TODO Wenn nicht gleiche RMX Version terminieren

				SocketConnector.nextRequestAllowed.set(true);
				break;
			case 4: // 0x04 - state info
				process0x04(message);
				SocketConnector.nextRequestAllowed.set(true);
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
				SocketConnector.nextRequestAllowed.set(true);
				break;
			case 40: // 0x28 - lok-info functions (f0 - f16)
				process0x28(message);
				SocketConnector.nextRequestAllowed.set(true);
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
	private static void process0x01(byte[] message) {

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
	private static void process0x04(byte[] message) {

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
	private static void process0x06(byte[] message) {

		if (Schedular.INIT_SUCESSFULL.get()) { // true -- init successfull

			// forward message to schedular
			Schedular.getSchedular().addMessageToRmxQueue(message);
			System.out.println("Message zu Schedular hinzugef¸gt!");

		} else { // false -- init not successfull

			// updates bus -> creates if not existing
			BusDepot.getBusDepot().updateBus(message);
		}

	}

	/**
	 * @param message
	 */
	private static void process0x08(byte[] message) {

	}

	/**
	 * @param message
	 */
	private static void process0x20(byte[] message) {

	}

	/**
	 * processsing lok-info speed and direction OPCODE 0x24. format
	 * <0x24><ADRH><ADRL><SPEED><DIR>
	 *
	 * @param message a message to process
	 */
	private static void process0x24(byte[] message) {

	}

	/**
	 * processsing lok-info functions (f0 - f16) OPCODE 0x28. format
	 * <0x28><ADRH><ADRL><F0F7><F8F15><F16F23>
	 *
	 * @param message a message to process
	 */
	private static void process0x28(byte[] message) {

	}

}