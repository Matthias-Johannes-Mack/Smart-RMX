package Utilities;

/**
 * Class containing constants for the Connection Package
 */
public final class Constants {

	/**
	 * *private Constructor to prevent instantiation
	 */
	private Constants() {
	}

	// Main
	public static final String OPERATING_SYSTEM = System.getProperty("os.name");
	// Console
	// put out an empty row
	public static final String EOF = "\n";
	public static final String EOF_SYS = System.getProperty("line.separator", EOF);
	// Connection
	public static byte RMX_VERSION = 0x01;
	public static final byte RMX_HEAD = 0x7c;
	public static final byte[] LOKDATENBANK_MESSAGE = new byte[] { RMX_HEAD, 0x04, 0x08, 0x01 };
	public static final byte[] INITALIZATION_MESSAGE = new byte[] { RMX_HEAD, 0x05, 0x03, 0x02, RMX_VERSION };
	public static final byte[] POSITIVE_HANDSHAKE = new byte[] { RMX_HEAD, 0x04, 0x00, 0x00 };

	// Bus
	public static final int NUMBER_SYSTEMADRESSES_PER_BUS = 112;
	public final static int NUMBER_BITS_PER_BUS = NUMBER_SYSTEMADRESSES_PER_BUS * 8; // each systemadress has 8 bits (1
																						// byte)
	public final static int NUMBER_OF_BUSSES = 1; // TODO wenn mehrere Busse -> variable erhöhen (für checkall)

}
