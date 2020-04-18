package matrix.matrixutilities;

import action.actionSequence.ActionSequence;
import action.actionSequence.ActionSequenceWrapper;
import bus.Bus;
import bus.BusDepot;
import matrix.byteMatrix.ByteRuleWrapper;
import utilities.Constants;

/**
 * class that holds utility methods for the matrixChecker
 */
public class MatrixUtil {

    /*-----------------------------------------------------------------------------------------------
      HELPER METHODS BITMATRIX
      - checkBitMatrixField
      - getNextHigherBusBitMatrix
     ----------------------------------------------------------------------------------------------*/

    /**
     * Returns (if existend) the ActionSequence of the given field in the bitMatrix specified by the bitIndex of the Row
     * and Column and der corresponding bit values
     * <p>
     * possible combinations of states (row, column): (0,0) - (1,0) - (1,0) - (1,1)
     *
     * @param bitValueRow    bitValue of the bit specified by the bitindex of the row
     * @param bitValueColumn bitValue of the bit specified by the bitindex of the column
     * @param bitMatrixField the field to check in the matrix
     * @return the actionSequence of the given state, returns null if no rule has been defined for the given state
     */
    public static ActionSequence checkBitMatrixField(boolean bitValueRow,
                                                     boolean bitValueColumn, ActionSequenceWrapper bitMatrixField) {

        ActionSequence actionSequence;

        // the other bit is currently set
        if (bitValueColumn) {
            // both conditions are true => get ActionSequence of point in matrix

            if (bitValueRow) {
                // bit value of row index =  1, bit value of column index is 1
                actionSequence = bitMatrixField.getActionSequence1And1();
            } else {
                // bit value of row index = 0, bit value of column index is 1
                actionSequence = bitMatrixField.getActionSequence0And1();
            }
        } else {
            // the other bit is not set
            if (bitValueRow) {
                // bit value of row index =  1, bit value of column index is 0
                actionSequence = bitMatrixField.getActionSequence1And0();
            } else {
                // bit value of row index =  0, bit value of column index is 0
                actionSequence = bitMatrixField.getActionSequence0And0();
            }

        }

        return actionSequence;

    }

    /**
     * Updates the given bus to the next higher bus if the bitIndex surpasses the last bit of the last bus.
     * Change happens exactly at the start of the new bus, for other bitindexes this method returns null
     *
     * @param bitIndex bitIndex to check
     * @return the bus of the given bitindex, null if bitIndex isnt exactly at a "changing point"
     */
    public static Bus getNextHigherBusBitMatrix(int bitIndex) {

        Bus bus = null;

        if (bitIndex % Constants.NUMBER_BITS_PER_BUS == 0) {
            // get current bus => + 1 since RMX starts counting at 1
            bus = BusDepot.getBusDepot().getBus(((bitIndex / Constants.NUMBER_BITS_PER_BUS) + 1));
        }

        return bus;
    }

     /*-----------------------------------------------------------------------------------------------
      HELPER METHODS BITMATRIX
      - checkByteMatrixField
      - getNextHigherBusByteMatrix
     ----------------------------------------------------------------------------------------------*/

    /**
     * Returns (if existend) the ActionSequence of the given field in the byteMatrix specified by the byteValue of the smaller index
     * and byteValue of the bigger index
     * <p>
     * - in row traversal the rowIndex is the smaller Index<br>
     * - in column traversal the columnIndex is the smaller Index
     *
     * @param byteValueSmall  byte Value of the smaller index
     * @param byteValueBig
     * @param byteMatrixField
     * @return the actionSequence of the given state, returns null if no rule has been defined for the given state
     */
    public static ActionSequence checkByteMatrixField(int byteValueSmall,
                                               int byteValueBig, ByteRuleWrapper byteMatrixField) {

        ActionSequence result = null;

        if (byteMatrixField != null) {
            // a rule has been defined -> check if a rule for the given states exists
            result = byteMatrixField.getActionSequenceByState(byteValueSmall, byteValueBig);
        }

        return result;
    }

    /**
     * Updates the given bus to the next higher bus if the byteIndex surpasses the last byte of the last bus.
     * Change happens exactly at the start of the new bus, for other byteIndexes this method returns null
     *
     * @param byteIndex byteIndex to check
     * @return the bus of the given byteindex, null if bitIndex isnt exactly at a "changing point"
     */
    public static Bus getNextHigherBusByteMatrix(int byteIndex) {

        Bus bus = null;

        if (byteIndex % Constants.NUMBER_SYSTEMADRESSES_PER_BUS == 0) {
            // get current bus => + 1 since RMX starts counting at 1
            bus = BusDepot.getBusDepot().getBus(((byteIndex / Constants.NUMBER_SYSTEMADRESSES_PER_BUS) + 1));
        }

        return bus;
    }
}
