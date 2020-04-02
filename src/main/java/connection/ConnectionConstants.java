package connection;

/**
 * Class containing constants for the Connection Package
 */
public final class ConnectionConstants {
	/**
	 * *private Constructor to prevent instantiation
	 */
	private ConnectionConstants() {
	}

	// Constants
	public static byte RMX_VERSION = 0x01;
	public static final byte RMX_HEAD = 0x7c;
	public static final byte[] LOKDATENBANK_MESSAGE = new byte[] { RMX_HEAD, 0x04, 0x08, 0x01 };
	public static final byte[] INITALIZATION_MESSAGE = new byte[] { RMX_HEAD, 0x05, 0x03, 0x02, RMX_VERSION };
	public static final byte[] EMERGENCY_STOP_MESSAGE = new byte[] { RMX_HEAD, 0x04, 0x03, 0x08 };
	public static final byte[] INITALIZATION_ANSWER = new byte[] { RMX_HEAD, 0x05, 0x03, 0x02, RMX_VERSION };
	public static final byte[] POSITIVE_HANDSHAKE = new byte[] { RMX_HEAD, 0x04, 0x00, 0x00 };
	// added negative handshake
	public static final byte[] NEGATIVE_HANDSHAKE = new byte[] { RMX_HEAD, 0x04, 0x01, 0x01 };
	// Connection running and no changes
	public static final byte[] NO_CHANGES = new byte[] { RMX_HEAD, 0x04, 0x04, 0x60 };
	// for the emergency stop answer
	public static final byte[] EMERGENCY_STOP_ANSWER = new byte[] { RMX_HEAD, 0x04, 0x00, 0x01 };
	// Depraced
	public static final byte[] F_HEAD = new byte[] { RMX_HEAD, 0x08, 0x1C };
}
