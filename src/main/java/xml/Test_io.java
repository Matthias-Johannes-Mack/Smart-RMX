package xml;

/**
 * Testtool für die XML-IO
 *
 * @author Matthias Mack 3316380
 */
public class Test_io {
    public static void main(String[] args) {
        // open the file
        XML_IO xml_io = new XML_IO();
        // read the xml
        XML_read xml_read = new XML_read(xml_io.getXML());
        xml_read.test();
    }
}
