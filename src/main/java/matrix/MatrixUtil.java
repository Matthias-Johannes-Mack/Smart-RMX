package matrix;

import Utilities.Constants;

public class MatrixUtil {

    /**
     * calculates the bitIndex of the given bus, systemadress and bit
     * Formula: (busId * Number_Systemadresses_per_bus) + (systemadress * 8) + bitIndexSystemadress
     *
     * @param busId
     * @param systemadress
     * @param bitIndexSystemadress
     * @return the bitIndex of the given bit at the given systemadress in the given bus
     */
    public static int calcBitIndex(int busId, int systemadress, int bitIndexSystemadress) {
        return (busId * Constants.NUMBER_SYSTEMADRESSES_PER_BUS) + (systemadress * 8) + bitIndexSystemadress;
    }

    /**
     * calculates the gaussian sum formula (sum of the first n following numbers)
     *
     * @param n
     * @return sum of the first n following numbers
     */
    public static int calcGauss(int n) {
        return (((n * n) + n) / 2);
    }
}