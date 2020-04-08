package xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.awt.*;
import java.io.File;
import java.util.Scanner;

/**
 * Class that returns a xml file selected via a file Dialog as org.w3c.dom.Document after checking it against xsd schema
 *
 * @author Matthias Mack 3316380
 */
public class XML_IO {
	/**
	 * 	component for the file dialog
	 */
	private static Component aComponent;
	/**
	 * 	xml File
	 */
	private static org.w3c.dom.Document xmlDoc;
	/**
	 * validity of the xml document regarding the xsd schema
	 */
	private static boolean validationResult = true;

	/**
	 * Constructor
	 */
	public XML_IO() {
		// set the filepath
		// filepath from the XML-File
		String filePath = openFileFromDialog();
		// if the file is chosen proceed
		if (filePath != null) {
			xmlDoc = returnXML(filePath);
			// if everything is fine, return message
			System.out.println("XML erfolgreich geladen!");
		} else {
			System.out.println("XML laden fehlgeschlagen! Erneut versuchen y/n?");
			// set the scanner
			// Scanner for the input
			Scanner in = new Scanner(System.in);
			// read the line
			// string for the value
			String retryStr = in.nextLine().toLowerCase();
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
	 * setter for validationResult
	 * @param value new value
	 */
	protected static void setValidationResult(boolean value) {
		validationResult = value;
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
	 * Method that reads the XML file and checks it against the xsd schema for validation
	 * @param filePath path of the xml file
	 * @return 	null if there was an parsing error or xml document is not valid regarding the xsd schema,
	 * 			else the document as org.w3c.dom.Document
	 */
	private static org.w3c.dom.Document returnXML(String filePath) {
		try {
			File xmlFile = new File(filePath);
			File schemaFile = new File("./src/main/resources/RuleSet.xsd");

			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

			//create the schema to check the xml document
			String constant = XMLConstants.W3C_XML_SCHEMA_NS_URI;
			SchemaFactory schemaFactory = SchemaFactory.newInstance(constant);
			Schema schema = schemaFactory.newSchema(schemaFile);

			//ignore comments and add xsd schema for validation of the xml document
			documentBuilderFactory.setIgnoringComments(true);
			documentBuilderFactory.setSchema(schema);

			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			//custom ErrorHandler for setting validationResult to false if xml document not valid regarding the schema
			documentBuilder.setErrorHandler(new customErrorHandler());

			org.w3c.dom.Document document = documentBuilder.parse(xmlFile);

			//throw exception if xml document is not valid regarding the xsd schema
			if(!validationResult) {
				throw new SAXException("XML Dokument entspricht nicht dem XSD Schema");
			}
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

/**
 * custom ErrorHandler for checking validating the xml document against the xsd schema
 * If the xml document is not valid regarding to the schema the ErrorHandler will set validationResult to false
 * and prevent the method to return the invalid xml document.
 */
class customErrorHandler implements ErrorHandler {
	@Override
	public void warning(SAXParseException exception) throws SAXException {
		System.err.println("Line " + exception.getLineNumber() + ": " + exception.getMessage());
		XML_IO.setValidationResult(false);
	}
	@Override
	public void error(SAXParseException exception) throws SAXException {
		System.err.println("Line " + exception.getLineNumber() + ": " + exception.getMessage());
		XML_IO.setValidationResult(false);

	}
	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		System.err.println("Line " + exception.getLineNumber() + ": " + exception.getMessage());
		XML_IO.setValidationResult(false);
	}
}
