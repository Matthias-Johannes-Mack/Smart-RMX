package Test;
import connection.*;

public class TestConnection {
	public TestConnection() {
		
	}

	public static void main(String[] args) {
		SocketConnector.Connect();
		System.out.println(SocketConnector.getConStateStr());
	}
}