package connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class that connects with a tcp socket and creates the connection to the RMX
 * PC-Zentrale
 * 
 * @author Matthias Mack 3316380
 */
public class SocketConnector {
	/**
	 * string for the ip. Here: localhost 127.0.0.1
	 */
	private static final String ip = "127.0.0.1";
	/**
	 * standard port for RMX: 950
	 */
	private static final int port = 950;
	/**
	 * create new InetSocketAddress to put ip and port together
	 */
	private static InetSocketAddress inet;

	/**
	 * enum for connection states
	 */
	protected enum conState {
		CONNECTING, RUNNING, DISCONNECTED, RECONNECT
	}

	/**
	 *  connection status variable
	 */
	private static conState conStateStr = conState.DISCONNECTED;
	/**
	 *  create the socket
	 */
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
	protected static conState getConStateStr() {
		return conStateStr;
	}

	/**
	 * Setter connection state
	 * 
	 * @param conStateStr Connection state
	 */
	protected static void setConStateStr(conState conStateStr) {
		SocketConnector.conStateStr = conStateStr;
	}

	/**
	 * Method, that connects to the RMX PC-Zentrale
	 */
	public static void Connect() {
		if (getConStateStr() == conState.DISCONNECTED || getConStateStr() == conState.RECONNECT) {
			// establish the connection
			try {
				setConStateStr(conState.CONNECTING);
				System.out.println("Verbinde zu Server " + ip + ":" + port);
				// checks if the server is alive
				socket = new Socket();
				// put the IP and port together
				inet = new InetSocketAddress(ip, port);
				socket.connect(inet);
				// set the connection state to running
				setConStateStr(conState.RUNNING);
				// starts the receiver
				Receiver.startReceiver();
				// initialize the sender
				Sender.initializeConnection();
				// show that server is connected
				System.out.println("-> Mit Server " + ip + ":" + port + " verbunden!");
				// start the server reload
				ServerReload.setLastServerResponse(System.currentTimeMillis());
				// Create a new ServerReload thread
				ServerReload serverReload = new ServerReload();
				serverReload.run();
			} catch (Exception e) {
				// set the status to disconnected
				setConStateStr(conState.DISCONNECTED);
				System.out.println("-> Server nicht erreichbar & " + getConStateStr());
				// retry the connection
				QuestionUtil.retry("Connect");
			}
		}
	}

	/**
	 * Closes the connection
	 * 
	 * @throws IOException
	 */
	public static void closeConnection() throws IOException {
		// when the connection is established kill it
		if (getConStateStr() == conState.RUNNING) {
			// set the connection string and put it out
			setConStateStr(conState.DISCONNECTED);
			socket.close();
			// put out the status
			System.out.println(getConStateStr());
		}
	}
}