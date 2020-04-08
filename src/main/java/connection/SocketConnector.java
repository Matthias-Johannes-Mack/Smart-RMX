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
	// -----------------------------------------
	// Singleton-Pattern START
	// -----------------------------------------

	/**
	 * Singleton instance of SocketConnector
	 */
	private static SocketConnector instance = null;

	/**
	 * private constructor to prevent instantiation
	 */
	private SocketConnector() {

	}

	/**
	 * Returns singleton SocketConnector instance
	 *
	 * @return SocketConnector Singleton instance
	 */
	public static synchronized SocketConnector getSocketConnector() {
		if (instance == null) {
			instance = new SocketConnector();
		}
		return instance;
	}

	// -----------------------------------------
	// Singleton-Pattern END
	// -----------------------------------------
	/**
	 * string for the ip. Here: localhost 127.0.0.1
	 */
	private final String ip = "127.0.0.1";
	/**
	 * standard port for RMX: 950
	 */
	private final int port = 950;
	/**
	 * create new InetSocketAddress to put ip and port together
	 */
	private InetSocketAddress inet;

	/**
	 * enum for connection states
	 */
	protected  enum conState {
		CONNECTING, RUNNING, DISCONNECTED, RECONNECT
	}

	/**
	 * connection status variable
	 */
	private conState conStateStr = conState.DISCONNECTED;
	/**
	 * create the socket
	 */
	private Socket socket;

	/*
	 * Is the last message to the server acknowledged by the server via a specific
	 * message one can send the next message to RMX Server
	 */
	protected AtomicBoolean nextRequestAllowed = new AtomicBoolean(true);

	/**
	 * Method, that returns the socket
	 *
	 * @return socket
	 */
	protected synchronized Socket getSocket() {
		return socket;
	}

	/**
	 * Getter connection state
	 * 
	 * @return conStateStr
	 */
	protected synchronized conState getConStateStr() {
		return conStateStr;
	}

	/**
	 * Setter connection state
	 * 
	 * @param conStateStr Connection state
	 */
	protected  void setConStateStr(conState conStateStr) {
		SocketConnector.getSocketConnector().conStateStr = conStateStr;
	}

	/**
	 * Method, that connects to the RMX PC-Zentrale
	 */
	public synchronized void Connect() {
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
	public synchronized void closeConnection() throws IOException {
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