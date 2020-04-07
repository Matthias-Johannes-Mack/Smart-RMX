package connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import Utilities.QuestionUtil;

/**
 * Class that connects with a tcp socket
 *
 * @author Matthias Mack 3316380
 */
public class SocketConnector {
	// localhost 127.0.0.1
	private static final String ip = "192.168.1.52";
	// standard port for RMX 950
	private static final int port = 950;
	// vars for the server response
	private static long lastServerResponse;
	private static InetSocketAddress inet;

	// enum for connection states
	protected enum conState {
		CONNECTING, RUNNING, DISCONNECTED, RECONNECT
	}

	// connection status
	private static conState conStateStr = conState.DISCONNECTED;
	// socket
	private static Socket socket;

	/*
	 * Is the last message to the server acknowledged by the server via a specific
	 * message one can send the next message to RMX Server
	 */
	protected static AtomicBoolean nextRequestAllowed = new AtomicBoolean(true);

	/**
	 * private Constructor to prevent instantiation
	 */
	private SocketConnector() {
	}

	/**
	 * Method, that returns the socket
	 *
	 * @return socket
	 */
	protected static Socket getSocket() {
		return socket;
	}

	/**
	 * Getter connection state
	 * 
	 * @return conStateStr
	 */
	public static conState getConStateStr() {
		return conStateStr;
	}

	/**
	 * Setter connection state
	 * 
	 * @param conStateStr Connection state
	 */
	private static void setConStateStr(conState conStateStr) {
		SocketConnector.conStateStr = conStateStr;
	}

	/**
	 * Method, that connects the Thread
	 */
	public static void Connect() {
		if (getConStateStr() == conState.DISCONNECTED || getConStateStr() == conState.RECONNECT) {
			setConStateStr(conState.CONNECTING);
			System.out.println("Verbinde zu Server " + ip + ":" + port);
			// establish the connection
			try {
				// checks if the server is alive
				socket = new Socket();
				inet = new InetSocketAddress(ip, port);
				socket.connect(inet);
				// set the connection state to running
				setConStateStr(conState.RUNNING);
				Receiver.startReceiver();
				// initialize
				Sender.initializeConnection();
				// show that server is connected
				System.out.println("-> Mit Server " + ip + ":" + port + " verbunden!");
				// checks if the server is alive
//				try {
//					SocketConnector.serverAlive();
//				} catch (InterruptedException | IOException e) {
//					// reload the server
//					setConStateStr(conState.DISCONNECTED);
//					QuestionUtil.retry_reload();
//				}
			} catch (Exception e) {
				setConStateStr(conState.DISCONNECTED);
				System.out.println("-> Server nicht erreichbar & " + getConStateStr());
				// retry the connection
				QuestionUtil.retry();
			}
		}
	}

	/**
	 * Method, that reloads the Thread
	 */
	public static void Reload() {
		// kill the threads
		Sender.setNull();
		Receiver.setNull();
		// reconnect
		Connect();
	}

	/**
	 * Method, that checks if the server has a timeout and then retries the
	 * connection
	 * 
	 * @throws IOException
	 * 
	 */
	public static void serverAlive() throws InterruptedException, IOException {
		// loop until connection lost
		while (!getConStateStr().equals(conState.RECONNECT)) {
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
			}
			Long now = System.currentTimeMillis();
			Long diff = now - getLastServerResponse();
			// if timeout retry connection > 10 seconds
			if (diff > 10000) {
				System.out.println("Server seit " + diff + " ms unerreichbar!");
				setConStateStr(conState.RECONNECT);
				throw new InterruptedException();
			}
		}
	}

	/**
	 * Closes the connection
	 * 
	 * @throws IOException
	 */
	public static void closeConnection() throws IOException {
		System.out.println("Attempt disconnect!");
		// when the connection is established kill it
		if (getConStateStr() == conState.RUNNING) {
			// set the connection string and put it out
			// Threads got killed instantly
			setConStateStr(conState.DISCONNECTED);
			socket.close();
			// put out the status
			System.out.println(getConStateStr());
		}
	}

	/**
	 * Getter for the server response
	 * 
	 * @return
	 */
	public static long getLastServerResponse() {
		return lastServerResponse;
	}

	/**
	 * Setter for the server response
	 * 
	 * @param lastServerResponse
	 */
	public static void setLastServerResponse(long lastServerResponse) {
		SocketConnector.lastServerResponse = lastServerResponse;
	}

}