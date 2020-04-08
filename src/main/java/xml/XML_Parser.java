package xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;

/**
 * class providing methods to parse the xml document
 */
class XML_Parser {

    /**
     * Method that reads the XML file and checks it against the xsd schema for validation
     * * @param filePath
     * * @return parsed document as org.w3c.dom.Document
     *
     * @param filePath path of the xml file
     * @return the parsed xml document
     * @throws SAXException                 exception if the xml file does not meet the criteria of the xsd schema
     * @throws IOException                  IO Exception
     * @throws ParserConfigurationException parser config exception
     */
    protected static org.w3c.dom.Document parseXMLDocument(String filePath) throws SAXException, IOException, ParserConfigurationException {
        //set true here in case the method is called multiple times through the user
        XML_IO.validationResult = true;

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
        if (!XML_IO.validationResult) {
            throw new SAXException("XML Dokument entspricht nicht dem XSD Schema");
        }
        return document;
    }


    /**
     * custom ErrorHandler for checking validating the xml document against the xsd schema
     * If the xml document is not valid regarding to the schema the ErrorHandler will set validationResult to false
     * and prevent the method to return the invalid xml document.
     */
    static class customErrorHandler implements ErrorHandler {
        @Override
        public void warning(SAXParseException exception) throws SAXException {
            System.err.println("Line " + exception.getLineNumber() + ": " + exception.getMessage());
            XML_IO.validationResult = false;
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            System.err.println("Line " + exception.getLineNumber() + ": " + exception.getMessage());
            XML_IO.validationResult = false;

        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            System.err.println("Line " + exception.getLineNumber() + ": " + exception.getMessage());
            XML_IO.validationResult = false;
        }
    }
}
