package Test;
import java.util.Scanner;

import connection.*;

public class TestConnection {
	static Scanner sc;
	public TestConnection() {
		
	}

	public static void main(String[] args) {
        SocketConnector.Connect();
		System.out.println("Eingabe:");
		sc =  new Scanner(System.in);
		String scanRes = sc.nextLine();
		if (scanRes.contains(" ") && scanRes.length() == 1) {
			System.out.println("Leertaste!");
		}
		System.out.println(SocketConnector.getConStateStr());
	}
}