package matrix.matrixutilities;

import utilities.Constants;

/**
 * provides Methods for matrix specific calculating
 */
public class MatrixCalcUtil {

    /**
     * calculates the bitIndex of the given bus, systemadress and bit for bit matrix
     * Formula: (busId * Number_Systemadresses_per_bus) + (systemadress * 8) + bitIndexSystemadress
     *
     * @param busId busId of the bus containing the bit
     * @param systemadress systemadress of the bit 0 - 111
     * @param bitIndexSystemadress bitindex of the bit in the byte 0-7
     * @return the bitIndex of the given bit at the given systemadress in the given bus
     */
    public static int calcBitIndex(int busId, int systemadress, int bitIndexSystemadress) {
        return (busId * Constants.NUMBER_SYSTEMADRESSES_PER_BUS) + (systemadress * 8) + bitIndexSystemadress;
    }

    /**
     * calculates the byteIndex of given bus and systemadress for byte matrix
     * @param busId id of the bus the byte is in
     * @param systemadress system address of the byte 0 - 111
     * @return the byteIndex of the given byte at the given systemadress and bus
     */
    public static int calcByteIndex(int busId, int systemadress) {
        return (busId * Constants.NUMBER_SYSTEMADRESSES_PER_BUS) + systemadress;
    }

    /**
     * calculates the gaussian sum formula (sum of the first n following numbers)
     *
     * @param n number whose gaussian sum should be a calculator
     * @return sum of the first n following numbers
     */
    public static int calcGauss(int n) {
        return (((n * n) + n) / 2);
    }

    /**
     * returns the SystemAddress for a given bitIndex
     * @param bitIndex index of a bit in the bit matrix
     * @return systemAddress of given bitIndex
     */
    public static int getSystemadressByBitIndex(int bitIndex) {
        return ((bitIndex % Constants.NUMBER_BITS_PER_BUS) / 8); // cuts decimal places
    }
}