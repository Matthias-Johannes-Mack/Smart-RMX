package connection;

import java.util.Scanner;

/**
 * Question dialog for the SocketConnector and the ServerReload
 *
 * @author Matthias Mack 3316380
 */
public class QuestionUtil {
	/**
	 * Method that retries the connection
	 * 
	 * @param questionType - String with the type of question inside
	 */
	protected static void retry(String questionType) {
		// retry the connection, if possible
		System.out.println("Erneut verbinden y/n?");
		// create scanner
		Scanner in = new Scanner(System.in);
		String retryStr = in.nextLine().toLowerCase();
		// reset the idle time
		ServerReload.setLastServerResponse(System.currentTimeMillis());
		if (retryStr != null) {
			switch (retryStr) {
			case "y":
				// if it is a connection recall then connect else reconnect
				if (questionType.equals("Connect")) {
					SocketConnector.getSocketConnector().Connect();
				} else {
					ServerReload.Reload();
				}
				break;
			// exit the programm
			case "n":
				System.exit(0);
				break;
			// if the string is false retry
			default:
				System.out.println("Falschen Wert eingegeben!");
				// if it is a connection recall then connect else reconnect
				if (questionType.equals("Connect")) {
					SocketConnector.getSocketConnector().Connect();
				} else {
					ServerReload.Reload();
				}
				break;
			}
		}

	}
}
