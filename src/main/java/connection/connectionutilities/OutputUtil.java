package connection.connectionutilities;

/**
 * Class containing Utility functions for Connections
 */
public class OutputUtil {
    /**
     * private Constructor to prevent instantiation
     */
    private OutputUtil(){}

    /**
     * prints a byte array in hex format on the console
     * @param message a byte array to be printed
     */
    public synchronized static void writeMsgToConsole(int[] message) {
        StringBuilder sb = new StringBuilder();
        for (int b : message) {
            // format: prefix 0x, 02: two digits, x: convert to hex number (lowercase)
            sb.append(String.format("0x%02x ", b));
        }
        System.out.println(sb.toString());
    }

}
