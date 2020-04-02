package connection;

/**
 * Class containing Utility functions for Connections
 */
public class ComUtilities {
    /**
     * private Constructor to prevent instantiation
     */
    private ComUtilities(){}

    /**
     * prints a byte array in hex format on the console
     * @param message a byte array to be printed
     */
    public synchronized static void writeMsgToConsole(byte[] message) {
        StringBuilder sb = new StringBuilder();
        for (byte b : message) {
            // format: prefix 0x, 02: two digits, x: convert to hex number (lowercase)
            sb.append(String.format("0x%02x ", b));
        }
        System.out.println(sb.toString());
    }
}
