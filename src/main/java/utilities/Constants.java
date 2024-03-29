package utilities;

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
	// Popup
	public static final String POPUP_TITLE = "IP & Port waehlen";
	public static final String POPUP_SUBMIT = "Weiter";
	public static final String POPUP_UNVALID_IP = "Die  eingegebene IP ist leider nicht valide! (255.255.255.255)";
	public static final String POPUP_UNVALID_PORT = "Der eingebene Port ist leider nicht valide! (Nur Zahlen)";
	// console
	public static final String RETRY_MESSAGE_RECONNECT = "Erneut verbinden?";
	public static final String RETRY_HEAD_RECONNECT = "Server nicht erreichbar";
	public static final String RETRY_MESSAGE_XML = "Datei erneut einlesen?";
	public static final String EOF = "\n";
	public static final String EOF_SYS = System.getProperty("line.separator", EOF);
	// Connection
	public static byte RMX_VERSION = 0x01;
	public static final int RMX_HEAD = 0x7c;
	public static final int[] LOKDATENBANK_MESSAGE = new int[] { RMX_HEAD, 0x04, 0x08, 0x01 };
	public static final int[] INITALIZATION_MESSAGE = new int[] { RMX_HEAD, 0x05, 0x03, 0x02, RMX_VERSION };
	public static final int[] POSITIVE_HANDSHAKE = new int[] { RMX_HEAD, 0x04, 0x00, 0x00 };

	// Bus
	public static final int OPCODE_WRITE_TO_BUS_ADRESS_FAKEMESSAGE = 0x99;
	public static final int OPCODE_WRITE_TO_BUS_ADRESS_RMXMESSAGE = 0x05;
	public static final int NUMBER_SYSTEMADRESSES_PER_BUS = 112;
	public final static int NUMBER_BITS_PER_BUS = NUMBER_SYSTEMADRESSES_PER_BUS * 8; // each systemadress has 8 bits (1
																						// byte)
	public static final int NUMBER_OF_BUSSES = 1; // TODO falls mehrere Busse unterstützt werden müssen -> variable erhöhen

}
