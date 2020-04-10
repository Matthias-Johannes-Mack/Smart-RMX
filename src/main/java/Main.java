import java.io.File;
import java.io.IOException;

import Utilities.Constants;
import connection.SocketConnector;
import schedular.Schedular;
import xml.Factory;
import xml.XML_IO;
import xml.XML_read;

/**
 * Class for controlling the whole Tool
 *
 * @author Matthias Mack 3316380
 */
public class Main {
	/**
	 * Main method
	 * 
	 * @param args - Arguments
	 */
	public static void main(String[] args) {
		// check the operating system
		if (Constants.OPERATING_SYSTEM.contains("Windows")) {
			if (args.length == 0) {
				try {
					Process p = Runtime.getRuntime().exec("cmd.exe /c start java -jar "
							+ (new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()))
									.getAbsolutePath()
							+ " cmd");
				} catch (IOException e) {
				}
			} else {
				// Execute commands
				command();
			}
		} else if (Constants.OPERATING_SYSTEM.contains("Linux")) {
			
		}

	}

	/**
	 * Method with commands
	 */
	private static void command() {
		// open the file
		XML_IO xml_io = XML_IO.getXML_IO();
		xml_io.startXmlReadInForUser();
		// read the xml
		XML_read xml_read = XML_read.getXML_read();
		xml_read.processXMLDocument(xml_io.getXML());

		// create the factory, actionDepot and the matrix
		Factory.createActionsAndMatrix();
		// create the connection
		// schedular MUSS vor Receiver Thread gestartet sein
		Schedular.getSchedular().startScheduling();
		SocketConnector.Connect();
	}
}
