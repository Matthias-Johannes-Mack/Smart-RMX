import java.io.File;
import java.io.IOException;

import Utilities.Constants;
import connection.SocketConnector;
import console.Console;
import console.PopUp_IP_Port;
import schedular.Schedular;
import xml.Factory;
import xml.XML_IO;

/**
 * Class for controlling the whole Tool
 *
 * @author Matthias Mack 3316380
 */
public class Main {
	private static boolean dialogShowed;

	/**
	 * Main method
	 * 
	 * @param args - Arguments
	 */
	public static void main(String[] args) {
		command();
	}

	/**
	 * Method with commands
	 */
	private static void command() {
		// run the console
		Console.getConsole().runConsole();
		head();
		// open the file
		XML_IO xml_io = XML_IO.getXML_IO();
		xml_io.startXmlReadInForUser();
		// create the factory, actionDepot and the matrix
		Factory.createActionsAndMatrix();
		// create the connection
		// schedular MUSS vor Receiver Thread gestartet sein
		Schedular.getSchedular().startScheduling();
		while (!PopUp_IP_Port.isDialogReady()) {
			if (!isDialogShowed()) {
				// show popup before connecting
				PopUp_IP_Port.showPopup();
				setDialogShowed(true);
			}
		}
		SocketConnector.Connect();
	}

	/**
	 * Method for the head
	 * 
	 */
	private static void head() {
		System.out.println(
				"^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		System.out.println(
				"--------------------------------------------------------------------------------------------");
		System.out.println("------------------------------------------Smart-RMX-----------------------------------");
		System.out.println(
				"--------------------------------------------------------------------------------------------");
		System.out.println(
				"^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
	}

	/**
	 * @return the dialogProcessed
	 */
	public static boolean isDialogShowed() {
		return dialogShowed;
	}

	/**
	 * @param dialogProcessed the dialogProcessed to set
	 */
	public static void setDialogShowed(boolean dialogProcessed) {
		Main.dialogShowed = dialogProcessed;
	}

}
