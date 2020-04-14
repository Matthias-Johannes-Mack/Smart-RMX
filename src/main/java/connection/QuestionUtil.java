package connection;

import javax.swing.JOptionPane;

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
		// shows the form for the user Input
		int userInput = JOptionPane.showConfirmDialog(null, "Erneut verbinden?", "Einverständnis",
				JOptionPane.YES_NO_OPTION);

		// reset the idle time
		ServerReload.setLastServerResponse(System.currentTimeMillis());

		switch (userInput) {
		case 0:
			// if it is a connection recall then connect else reconnect
			if (questionType.equals("Connect")) {
				SocketConnector.Connect();
			} else {
				ServerReload.Reload();
			}
			break;
		// exit the programm
		case 1:
			System.exit(0);
			break;
		// if the string is false retry
		default:
			System.out.println("Falschen Wert eingegeben!");
			// if it is a connection recall then connect else reconnect
			if (questionType.equals("Connect")) {
				SocketConnector.Connect();
			} else {
				ServerReload.Reload();
			}
			break;
		}
	}

}