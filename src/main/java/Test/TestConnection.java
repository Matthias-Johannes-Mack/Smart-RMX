package Test;

import java.io.IOException;
import java.util.Scanner;

import connection.ConnectionConstants;
import connection.Sender;
import connection.SocketConnector;

public class TestConnection {
	static Scanner sc;

	public TestConnection() {

	}

	public static void main(String[] args) {
		SocketConnector.Connect();
		System.out.println("Leertaste für Notaus drücken:");
		emergencyStop();
//		System.out.println(SocketConnector.getConStateStr());
	}

	/**
	 * Method, that listens to a whitespace and the puts out a emergency stop
	 */
	private static void emergencyStop() {
		sc = new Scanner(System.in);
		String scanRes = sc.nextLine();
		// exit programm if kill is entered
		if (scanRes.toLowerCase().contains("help") && scanRes.length() == 4) {
			System.out.println("-----------Hilfe-----------");
			System.out.println("Kommandos: Leertaste -> Nothalt; exit-> Programmende;");
			System.out.println("----------------------");
			emergencyStop();
		} else if (scanRes.toLowerCase().contains("exit") && scanRes.length() == 4) {
			try {
				SocketConnector.closeConnection();
			} catch (IOException e) {
			}
			System.exit(0);
		}
		if (!scanRes.contains(" ") && scanRes.length() == 1) {
			System.out.println("Nur Leertaste akzeptiert!");
			emergencyStop();
		} else {
			Sender.addMessageQueue(ConnectionConstants.EMERGENCY_STOP_MESSAGE);
			emergencyStop();
		}

	}
}