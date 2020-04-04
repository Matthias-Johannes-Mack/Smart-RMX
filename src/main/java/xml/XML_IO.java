package xml;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.File;
import java.util.Scanner;

/**
 * Class that returns a xml file via a file Dialog
 *
 * @author Matthias Mack 3316380
 */
class XML_IO {

	// component for the file dialog
	private static Component aComponent;
	// filepath from the XML-File
	private static String filePath;
	// string for the value
	private static String retryStr;
	// Scanner for the input
	private static Scanner in;
	// xml File
	private static org.w3c.dom.Document xmlDoc;

	public XML_IO() {
		// set the filepath
		filePath = openFileFromDialog();
		// if the file is choosen proceed
		if (filePath != null) {
			xmlDoc = returnXML(filePath);
			// if everthings fine, return message
			System.out.println("XML erfolgreich geladen!");
		} else {
			System.out.println("XML laden fehlgeschlagen! Erneut versuchen y/n?");
			// set the scanner
			in = new Scanner(System.in);
			// read the line
			retryStr = in.nextLine().toLowerCase();
			// check the decision
			switch (retryStr) {
			// restart the whole thing
			case "y":
				XML_IO obj = new XML_IO();
				break;
			case "n":
				System.exit(0);
			default:
				System.exit(0);
			}
		}
	}

	/**
	 * Method which opens the file and returns the filename
	 */
	private static String openFileFromDialog() {
		try {
			// Create a filechooser
			final JFileChooser fc = new JFileChooser();
			// sets the name of the file dialog
			fc.setDialogTitle("Bitte XML-Datei wählen");
			// set the filter
			fc.setAcceptAllFileFilterUsed(false);
			// set the thing to xml
			FileNameExtensionFilter filter = new FileNameExtensionFilter(".xml", "xml");
			fc.addChoosableFileFilter(filter);
			// if the file is choosed, return the name
			int returnVal = fc.showOpenDialog(aComponent);
			// get the path
			if (returnVal == 0) {
				return fc.getSelectedFile().getAbsolutePath();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Method that reads the XML file
	 * 
	 * @param filePath
	 */
	private static org.w3c.dom.Document returnXML(String filePath) {
		try {
			File file = new File(filePath);
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			org.w3c.dom.Document document = documentBuilder.parse(file);
			return document;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get the XML file
	 */
	public static org.w3c.dom.Document getXML() {
		return xmlDoc;
	}
}
