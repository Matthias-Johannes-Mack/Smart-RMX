package tests_IO;

import static org.junit.Assert.*;

import org.junit.Test;

import xml.XML_IO;
import xml.XML_read;
/**
 * Class that tests the IO
 *
 * @author Matthias Mack 3316380
 */
public class TestIO {

	@Test
	/**
	 * Class for testing the xml_io
	 */
	public void testIO() {
        // open the file
        XML_IO xml_io = new XML_IO();
        // read the xml
        XML_read xml_read = new XML_read(xml_io.getXML());
        xml_read.test();
	}
}
