package connection;

import Utilities.QuestionUtil;
import connection.SocketConnector.conState;

/**
 * Class that reloads the server
 *
 * @author Matthias Mack 3316380
 */
public class ServerReload implements Runnable {

	private static long lastServerResponse;

	@Override
	public void run() {
		// loop until Reconnect flag is
		while (SocketConnector.getConStateStr().equals(conState.RUNNING)) {
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
			}
			Long now = System.currentTimeMillis();
			Long diff = now - getLastServerResponse();
			// if timeout retry connection > 10 seconds
			if (diff > 10000) {
				System.out.println("Server seit " + diff + " ms unerreichbar!");
				// needed, for restarting server
				SocketConnector.setConStateStr(conState.RECONNECT);
				// call the retry form
				QuestionUtil.retry_reload();
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
		SocketConnector.Connect();
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
		ServerReload.lastServerResponse = lastServerResponse;
	}
}
