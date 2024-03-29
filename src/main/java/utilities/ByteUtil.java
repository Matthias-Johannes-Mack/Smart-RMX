package utilities;

import java.util.BitSet;

/**
 * class that holds utility methods for dealing with byte calculations
 */
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
     * @param lowByte  low byte: byte to the right with the smaller x in 2^x
     * @param highByte high byte: byte to the left with the higher x in 2^x
     * @return int - int value of the two bytes
     */
    public static int convertToInt(byte lowByte, byte highByte) {
        int solution = ((int) highByte << 8) | ((int) lowByte & 0xFF);
        return solution;
    }

    /**
     * checks if bit is set in a given byte
     *
     * @param value    byte value to check
     * @param bitIndex counting from 0-7
     * @return true = if set, else false
     */
    public static boolean bitIsSet(int value, int bitIndex) {
        BitSet bitSet = BitSet.valueOf(new long[]{value});

        return (bitSet.get(bitIndex) == true);
    }

    /**
     * sets bit at the given index with given value
     *
     * @param currentByte
     * @param bitIndex
     * @param value
     * @return
     */
    public static int setBitAtPos(int currentByte, int bitIndex, int value) {
        int mask = 1 << bitIndex;
        return (currentByte & ~mask) | ((value << bitIndex) & mask);
    }

    /**
     * toggles bit at the given index
     *
     * @param currentByte byte to toggle the bit
     * @param bitIndex Index of the bit to toggle
     * @return
     */
    public static int toggleBitAtPos(int currentByte, int bitIndex) {
        return (currentByte ^= 1 << bitIndex);
    }

    /**
     * converts an integer array with 8 indexes (set to 0 or 1) to an byte
     * the indexes of the integer array correspond to the bitIndexes.
     *
     * @param byteArray integer array with 8 indexes that represents the value of an byte written with bits
     * @return byteValue as an int
     */
    public static int getByteByByteArray(Integer[] byteArray) {

        StringBuilder sb = new StringBuilder();

        for (int i = 7; i >= 0; --i) {
            sb.append(byteArray[i]);
        }

        return Integer.parseInt(sb.toString(), 2);

    }

    /**
     * converts an array with integer values to an array with byte values
     * @param values array to convert
     * @return the converted byte array
     */
    public static byte[] convertIntArrayToByteArray(int[] values) {

        byte[] arrayByte = new byte[values.length];

        for (int i = 0; i < values.length; ++i) {
            arrayByte[i] = (byte) (values[i]);
        }

        return arrayByte;
    }
}