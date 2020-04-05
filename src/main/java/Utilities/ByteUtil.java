package Utilities;

public class ByteUtil {
    /**
     * Method which converts a int into 2 bytes
     *
     * @param val = integer as value
     * @return = returns a byte Array
     */
    public static byte[] convertToBytes(int val) {
        byte[] twoBytes = new byte[2];

        twoBytes[1] = (byte) (val & 0xFF);
        twoBytes[0] = (byte) ((val >> 8) & 0xFF);

        return twoBytes;
    }

    /**
     * Method which converts 2 bytes (High and Low Byte) to an integer
     *
     * @param lowByte low byte
     * @param highByte high byte
     * @return int - int value of the two bytes
     */
    public static int convertToInt(byte lowByte, byte highByte) {
        int solution = ((int) highByte << 8) | ((int) lowByte & 0xFF);
        return solution;
    }

    /**
     * checks if bit is set in given message
     *
     * @param value    byte value to check
     * @param bitIndex counting from 0-7
     * @return true = if set, else false
     */
    public static boolean bitIsSet(byte value, int bitIndex) {
        return (value & (1 << bitIndex)) != 0;
    }

}