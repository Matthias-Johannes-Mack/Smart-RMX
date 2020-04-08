package connection;

import connection.SocketConnector.conState;

/**
 * Class that reloads the server
 *
 * @author Matthias Mack 3316380
 */
public class ServerReload implements Runnable {
	// return the last server response
	private static long lastServerResponse;

	/**
	 * Runnable to check at runtime if the server is connected or not!
	 */
	@Override
	public void run() {
		// loop until Reconnect flag is set
		while (SocketConnector.getConStateStr().equals(conState.RUNNING)) {
			// sleep 5 seconds to create a delay
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
			}
			// create two long variables with the actual time in digits
			// and the difference between now and the time send by the status
			Long now = System.currentTimeMillis();
			Long diff = now - getLastServerResponse();
			// if timeout retry connection > 10 seconds
			if (diff > 10000) {
				// put out warning
				System.out.println("Server seit " + diff + " ms unerreichbar!");
				// needed, for restarting server
				SocketConnector.setConStateStr(conState.RECONNECT);
				// call the retry form
				QuestionUtil.retry("Reload");
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
	 * @return long - the timestamp in millis
	 */
	public static long getLastServerResponse() {
		return lastServerResponse;
	}

	/**
	 * Setter for the server response
	 * 
	 * @param lastServerResponse - sets the stimestamp in millis
	 */
	public static void setLastServerResponse(long lastServerResponse) {
		ServerReload.lastServerResponse = lastServerResponse;
	}
}
