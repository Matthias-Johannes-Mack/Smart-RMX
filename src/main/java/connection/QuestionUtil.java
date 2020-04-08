package connection;

import java.util.Scanner;

import connection.ServerReload;
import connection.SocketConnector;

/**
 * Libary with functions for all sorts of usages
 *
 * @author Matthias Mack 3316380
 */
public class QuestionUtil {
	/**
	 * Method that retries the connection
	 */
	public static void retry() {
		// retry the connection, if possible
		System.out.println("Erneut verbinden y/n?");
		Scanner in = new Scanner(System.in);
		String retryStr = in.nextLine().toLowerCase();
		// reset the idle time
		ServerReload.setLastServerResponse(System.currentTimeMillis());
		if (retryStr != null) {
			switch (retryStr) {
			case "y":
				SocketConnector.Connect();
				break;
			case "n":
				System.exit(0);
				break;
			// if the string is false retry
			default:
				System.out.println("Falschen Wert eingegeben!");
				SocketConnector.Connect();
				break;
			}
		}
	}

	/**
	 * Method that retries the connection after a reload
	 */
	public static void retry_reload() {
		// retry the connection, if possible
		System.out.println("Erneut verbinden y/n?");
		Scanner in = new Scanner(System.in);
		String retryStr = in.nextLine().toLowerCase();
		// reset the idle time
		ServerReload.setLastServerResponse(System.currentTimeMillis());
		if (retryStr != null) {
			switch (retryStr) {
			case "y":
				ServerReload.Reload();
				break;
			case "n":
				System.exit(0);
				break;
			// if the string is false retry
			default:
				System.out.println("Falschen Wert eingegeben!");
				ServerReload.Reload();
				break;
			}
		}
	}
}
