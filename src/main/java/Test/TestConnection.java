package Test;

import java.util.Scanner;


public class TestConnection {
	static Scanner sc;

	public TestConnection() {

	}

	public static void main(String[] args) {
//		SocketConnector.Connect();
		System.out.println("Leertaste für Notaus drücken:");
		emergencyStop();
//		System.out.println(SocketConnector.getConStateStr());
	}

	private static void emergencyStop() {
		sc = new Scanner(System.in);
		String scanRes = sc.nextLine();
		if (! scanRes.contains(" ") && scanRes.length() == 1) {
			emergencyStop();
		} else {
			System.out.println("Leertaste!");
			
		}
	}
}