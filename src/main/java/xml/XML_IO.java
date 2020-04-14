package xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import console.Console;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Scanner;

/**
 * Class that returns a xml file selected via a file Dialog as
 * org.w3c.dom.Document after checking it against xsd schema
 *
 * @author Matthias Mack 3316380
 */
public class XML_IO {
	/**
	 * xml_parser instrance
	 */
	private XML_Parser xml_parser;

	// Singleton-Pattern START -----------------------------------------

	/**
	 * Singleton instance of XML_IO
	 */
	private static XML_IO instance = null;

	/**
	 * private constructor to prevent instantiation
	 */
	private XML_IO() {

	}

	/**
	 * Returns singleton XML_IO instance
	 *
	 * @return XML_IO Singleton instance
	 */
	public static synchronized XML_IO getXML_IO() {
		if (instance == null) {
			instance = new XML_IO();
			instance.xml_parser = XML_Parser.getXML_Parser();
		}

		return instance;
	}

	// Singleton-Pattern END ________________________________________________

	/**
	 * xml File
	 */
	private org.w3c.dom.Document xmlDoc;
	/**
	 * validity of the xml document regarding the xsd schema
	 */
	protected boolean validationResult;
	/**
	 * filepath of the xml file the user wants to read in
	 */
	private String filePath = null;
	/**
	 * indicates whether the xml document was parsed successfully without schema or
	 * file exceptions
	 */
	private boolean xmlDocumentSuccessfullyParsed = false;

	/**
	 * getter for validation result
	 * 
	 * @return value of validationResult
	 */
	public boolean getValidationResult() {
		return validationResult;
	}

	/**
	 * setter for validationResult
	 * 
	 * @param validationResult new value of validationResult
	 */
	public void setValidationResult(boolean validationResult) {
		this.validationResult = validationResult;
	}

	/**
	 * Get the XML file
	 */
	public org.w3c.dom.Document getXML() {
		return xmlDoc;
	}

	/**
	 * method to start the process of reading in the xml file for the user
	 */
	public void startXmlReadInForUser() {
		// set the filepath from the XML-File
		while (filePath == null) {
			readInFilePathXML();
		}

		// parses the xml file
		while (!xmlDocumentSuccessfullyParsed) {
			readInXmlFile(filePath);
		}
		System.out.println("XML erfolgreich geladen!");
	}

	/**
	 * opens file dialog via openFileDialog() and checks if filepath was read in
	 * successfully, if not opens error handling in console
	 */
	private void readInFilePathXML() {
		filePath = openFileDialog();

		if (filePath == null) {
			System.out.println("XML laden fehlgeschlagen! Erneut versuchen y/n?");
			errorHandlerConsole();
		}
	}

	/**
	 * reads in and parses the xml file via parseXMLDocument() and saves it to
	 * xmlDoc If parseXMLDocument() throws an exception the method will open opens
	 * error handling in console via errorHandlerConsole()
	 *
	 * @param filePath file path of the xml document to be read in and parsed
	 */
	private void readInXmlFile(String filePath) {
		try {
			xmlDoc = xml_parser.parseXMLDocument(filePath);
			xmlDocumentSuccessfullyParsed = true;
		} catch (SAXException e) {
			System.out.println("XML laden fehlgeschlagen! Die Datei entspricht nicht dem Smart RMX Datei Schema!");
			errorHandlerConsole();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.out.println("XML laden fehlgeschlagen!");
			errorHandlerConsole();
		}
	}

	/**
	 * outputs the option for the user to either retry reading in a file or exit the
	 * program in the console via errorHandlerConsole()
	 */
	private void errorHandlerConsole() {
		// shows the form for the user Input
		int userInput = JOptionPane.showConfirmDialog(null, "Erneut versuchen?", "Einverst‰ndnis",
				JOptionPane.YES_NO_OPTION);
		if (userInput == 0) {
			// restart at read in file
			readInFilePathXML();
		} else if (userInput == 1) {
			System.exit(0);
		}
	}

	/**
	 * opens file dialog for the user to select the filepath of the file to be read
	 *
	 * @return filepath
	 */
	private String openFileDialog() {
		try {
			// Create a filechooser
			final JFileChooser fc = new JFileChooser();
			// sets the name of the file dialog
			fc.setDialogTitle("Bitte XML-Datei w√§hlen");
			// set the filter
			fc.setAcceptAllFileFilterUsed(false);
			// set the thing to xml
			FileNameExtensionFilter filter = new FileNameExtensionFilter(".xml", "xml");
			fc.addChoosableFileFilter(filter);
			// if the file is choosed, return the name
			int returnVal = fc.showOpenDialog(null);
			// get the path
			if (returnVal == 0) {
				return fc.getSelectedFile().getAbsolutePath();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
