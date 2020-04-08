package xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Class that returns a xml file selected via a file Dialog as org.w3c.dom.Document after checking it against xsd schema
 *
 * @author Matthias Mack 3316380
 */
public class XML_IO {
     /**
     * xml File
     */
    private static org.w3c.dom.Document xmlDoc;
    /**
     * validity of the xml document regarding the xsd schema
     */
    private static boolean validationResult;
    /**
     * filepath of the xml file the user wants to read in
     */
    private static String filePath = null;
    /**
     * indicates whether the xml document was parsed successfully without schema or file exceptions
     */
    private static boolean xmlDocumentSuccessfullyParsed = false;

    /**
     * Constructor
     */
    public XML_IO() {
        // set the filepath from the XML-File
        while (filePath == null) {
            readInFilePathXML();
        }

        //parses the xml file
        while (!xmlDocumentSuccessfullyParsed) {
            readInXmlFile(filePath);
        }
        System.out.println("XML erfolgreich geladen!");
    }

    /**
     * opens file dialog via openFileDialog() and checks if filepath was read in successfully,
     * if not opens error handling in console
     */
    private static void readInFilePathXML() {
        filePath = openFileDialog();

        if (filePath == null) {
            System.out.println("XML laden fehlgeschlagen! Erneut versuchen y/n?");
            errorHandlerConsole();
        }

    }

    /**
     * reads in and parses the xml file via parseXMLDocument() and saves it to xmlDoc
     * If parseXMLDocument() throws an exception the method will open opens error handling in console via errorHandlerConsole()
     *
     * @param filePath file path of the xml document to be read in and parsed
     */
    private static void readInXmlFile(String filePath) {
        try {
            xmlDoc = parseXMLDocument(filePath);
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
     * outputs the option for the user to either retry reading in a file or exit the program in the console via errorHandlerConsole()
     */
    private static void errorHandlerConsole() {
        System.out.println("Erneut versuchen y/n?");
        Scanner in = new Scanner(System.in);
        String retryStr = in.nextLine().toLowerCase();

        if ("y".equals(retryStr)) {
            // restart at read in file
            readInFilePathXML();
        } else {
            System.exit(0);
        }
    }


    /**
     * setter for validationResult
     */
    protected static void setValidationResultFalse(){
        validationResult = false;
    }

    /**
     * opens file dialog for the user to select the filepath of the file to be read
     *
     * @return filepath
     */
    private static String openFileDialog() {
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

    /**
     * Method that reads the XML file and checks it against the xsd schema for validation
     * * @param filePath
     * * @return 	null if there was an parsing error or xml document is not valid regarding the xsd schema,
     * * 			else the document as org.w3c.dom.Document
     *
     * @param filePath path of the xml file
     * @return the parsed xml document
     * @throws SAXException                 exception if the xml file does not meet the criteria of the xsd schema
     * @throws IOException                  IO Exception
     * @throws ParserConfigurationException parser config exception
     */
    private static org.w3c.dom.Document parseXMLDocument(String filePath) throws SAXException, IOException, ParserConfigurationException {
        //set thrue here in case the method is called multiple times through the user
        validationResult = true;

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

        //parse the document
        org.w3c.dom.Document document = documentBuilder.parse(xmlFile);

        //throw exception if xml document is not valid regarding the xsd schema
        if (!validationResult) {
            throw new SAXException("XML Dokument entspricht nicht dem XSD Schema");
        }
        return document;
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
        XML_IO.setValidationResultFalse();
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        System.err.println("Line " + exception.getLineNumber() + ": " + exception.getMessage());
        XML_IO.setValidationResultFalse();

    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        System.err.println("Line " + exception.getLineNumber() + ": " + exception.getMessage());
        XML_IO.setValidationResultFalse();
    }
}
