import java.io.File;
import java.io.IOException;

import connection.SocketConnector;
import schedular.Schedular;
import xml.Factory;
import xml.XML_IO;
import xml.XML_read;

/**
 * Main method, that controlls the whole tool
 *
 * @author TeamRMX
 */
public class Main {
	/**
	 * Main method
	 * 
	 * @param args - Arguments
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			try {
				Process p = Runtime.getRuntime().exec("cmd.exe /c start java -jar "
						+ (new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()))
								.getAbsolutePath()
						+ " cmd");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// code to be executed
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
}
