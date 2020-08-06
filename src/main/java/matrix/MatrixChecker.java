package matrix;

import action.actionSequence.ActionSequence;
import bus.Bus;
import bus.BusDepot;
import matrix.bitMatrix.BitMatrix;
import matrix.byteMatrix.ByteMatrix;
import matrix.matrixutilities.MatrixCalcUtil;
import matrix.matrixutilities.MatrixUtil;
import utilities.ByteUtil;
import utilities.Constants;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * class that is responsible for traversing the BitMatrix and ByteMatrix with incoming changes
 */
public class MatrixChecker {

    // Singleton-Pattern START -----------------------------------------

    /**
     * Singleton instance of matrixChecker
     */
    private static MatrixChecker matrixCheckerInstance;

    /**
     * private constructor to prevent instantiation
     */
    private MatrixChecker() {

        bitMatrix = BitMatrix.getMatrix();
        byteMatrix = ByteMatrix.getMatrix();
        busDepot = BusDepot.getBusDepot();

    }

    /**
     * Returns singleton matrixChecker instance
     *
     * @return MatrixChecker Singleton instance
     */
    public static synchronized MatrixChecker getMatrixChecker() {
        if (matrixCheckerInstance == null) {
            matrixCheckerInstance = new MatrixChecker();

        }
        return matrixCheckerInstance;
    }

    // Singleton-Pattern END ________________________________________________


    /**
     * BitMatrix that contains defined rules for each bit
     */
    private BitMatrix bitMatrix;

    /**
     * ByteMatrix that contains defined rules for each byte
     */
    private ByteMatrix byteMatrix;

    /**
     * busDepot handling the access to each bus
     */
    private BusDepot busDepot;



     /*-----------------------------------------------------------------------------------------------
      METHODS FOR CHECKING ALL MATRIX FIELDS
      - checkAllFields()

      HELPER METHODS
      - checkAllFieldsBitMatrix()
      - checkAllFieldsByteMatrix()
     ----------------------------------------------------------------------------------------------*/

    /**
     * Method, that checks all fields of the bit and byte matrix
     *
     * @return List of ActionSequences that have been triggered, the list is empty if no ActionSequences have been triggered
     */
    public List<ActionSequence> checkAllFields() {

        List<ActionSequence> result = new ArrayList<>();

        result.addAll(checkAllFieldsBitMatrix());
        result.addAll(checkAllFieldsByteMatrix());

        return result;
    }

     /*-----------------------------------------------------------------------------------------------
      HELPER METHODS
      - checkAllFieldsBitMatrix()
      - checkAllFieldsByteMatrix()
     ----------------------------------------------------------------------------------------------*/

    /**
     * Method, that checks all fields of the matrix
     *
     * @return List of ActionSequences that have been triggered, the list is empty if no ActionSequences have been triggered
     */
    private List<ActionSequence> checkAllFieldsBitMatrix() {

        List<ActionSequence> result = new ArrayList<>();

        Bus currentBusRow = null;
        Bus currentBusColumn = null;

        // pointer for the field in the matrix
        int fieldIndexBitMatrix = 0;
        // outer for loop goes along the row
        for (int bitIndexRow = 0; bitIndexRow < (Constants.NUMBER_OF_BUSSES * Constants.NUMBER_BITS_PER_BUS); bitIndexRow++) {

            // updates the bus if the bitIndexRow is in the next higher bus
            Bus tempBusRow;
            currentBusRow = ((tempBusRow = MatrixUtil.getNextHigherBusBitMatrix(bitIndexRow)) != null) ? tempBusRow : currentBusRow;

            //inner for loop goes along the column
            for (int bitIndexColumn = 0; bitIndexColumn < bitIndexRow + 1; bitIndexColumn++) {
                // only check conditions if a ActionSequenceWrapper is in the matrix (= a rule is defined)

                // updates the bus if the bitIndexColumn is in the next higher bus
                Bus tempBusColumn;
                currentBusColumn = ((tempBusColumn = MatrixUtil.getNextHigherBusBitMatrix(bitIndexColumn)) != null) ? tempBusColumn : currentBusColumn;

                if (bitMatrix.getBitMatrixField(fieldIndexBitMatrix) != null) {
                    // an ActionSequenceWrapper is in the field

                    // get Variables needed for check
                    int systemadress_bitIndexRow = MatrixCalcUtil.getSystemadressByBitIndex(bitIndexRow);
                    int systemadress_bitIndexColumn = MatrixCalcUtil.getSystemadressByBitIndex(bitIndexColumn);
                    boolean bitValueRow = currentBusRow.isBitSet(systemadress_bitIndexRow, (bitIndexRow % 8));
                    boolean bitValueColumn = currentBusColumn.isBitSet(systemadress_bitIndexColumn, (bitIndexColumn % 8));

                    // returns the actionSequence by the current state (00, 10, 01 or 11)
                    ActionSequence actionSequence = MatrixUtil.checkBitMatrixField(bitValueRow,
                            bitValueColumn, bitMatrix.getBitMatrixField(fieldIndexBitMatrix));

                    if (actionSequence != null) {
                        // ActionSequence for point exists = a rule has been defined

                        result.add(actionSequence);
                    }

                }

                fieldIndexBitMatrix++; // go to the next field

            } // inner for-loop
        } // outer for-loop

        return result;
    }

    /**
     * Checks all fields in the ByteMatrix
     *
     * @return List of ActionSequences that have been triggered
     */
    private List<ActionSequence> checkAllFieldsByteMatrix() {
        List<ActionSequence> result = new ArrayList<>();

        Bus currentBusRow = null;
        Bus currentBusColumn = null;

        // pointer for the field in the matrix
        int fieldIndexByteMatrix = 0;
        // outer for loop goes along the row
        for (int byteIndexRow = 0; byteIndexRow < (Constants.NUMBER_OF_BUSSES * Constants.NUMBER_SYSTEMADRESSES_PER_BUS); byteIndexRow++) {

            // updates the bus if the byteIndexRow is in the next higher bus
            Bus tempBusRow;
            currentBusRow = ((tempBusRow = MatrixUtil.getNextHigherBusByteMatrix(byteIndexRow)) != null) ? tempBusRow : currentBusRow;

            //inner for loop goes along the column
            for (int byteIndexColumn = 0; byteIndexColumn < byteIndexRow + 1; byteIndexColumn++) {
                // only check conditions if a ByteRuleWrapper is in the matrix (= a rule is defined)

                // updates the bus if the byteIndexColumn is in the next higher bus
                Bus tempBusColumn;
                currentBusColumn = ((tempBusColumn = MatrixUtil.getNextHigherBusByteMatrix(byteIndexColumn)) != null) ? tempBusColumn : currentBusColumn;

                // byte needed for checking
                int byteValueSmall;
                int byteValueBig;

                // determine if row or column has the smaller byteIndex
                if (byteIndexRow < byteIndexColumn) {
                    byteValueSmall = currentBusRow.getCurrentByte(byteIndexRow);
                    byteValueBig = currentBusColumn.getCurrentByte(byteIndexColumn);
                } else {
                    byteValueSmall = currentBusColumn.getCurrentByte(byteIndexColumn);
                    byteValueBig = currentBusRow.getCurrentByte(byteIndexRow);
                }

                // check ByteMatrixField with given values
                ActionSequence actionSequenceByteMatrix = MatrixUtil.checkByteMatrixField(byteValueSmall, byteValueBig, byteMatrix.getByteMatrixField(fieldIndexByteMatrix));

                if (actionSequenceByteMatrix != null) {
                    // ActionSequence for point exists = a rule has been defined
                    result.add(actionSequenceByteMatrix);
                }

                fieldIndexByteMatrix++; // go to the next field

            } // inner for-loop
        } // outer for-loop

        return result;
    }

    /*-----------------------------------------------------------------------------------------------
      METHODS FOR CHECKING SPECIFIC CHANGES
      - check()

      HELPER METHODS
      - traverseBitAndByteMatrixAndCheck()
      - traverseBitMatrixAndCheck()
     ----------------------------------------------------------------------------------------------*/

    /**
     * checks if for the given changes a rule is defined in the matrix. If so it returns the triggered ActionSequences
     * in a list. The List is empty if no ActionSequences are triggered.
     * <p>
     * format of lastChanges (index 0 to 7)
     * - 1: bit changed to 1
     * - 0: bit changed to 0
     * - -1: nothing changed
     *
     * @param busId        the busId of the bus the changes
     * @param systemadress the systemadress of the changes
     * @param lastChanges  the changes of the given systemadress
     * @return List of ActionSequences that have been triggered, the list is empty if no ActionSequences have been triggered
     */
    public List<ActionSequence> check(int busId, int systemadress, Integer[] lastChanges) {

        // contains ActionSequences that are triggered
        List<ActionSequence> result = new ArrayList<>();

        // check the byteMatrix one once for each incoming lastChanges-Array
        // since it contains the changes for exactly the given one byte
        boolean checkByteMatrix = true;

        // iterates over each "bit" of lastChanged
        for (int i = 0; i < 8; i++) {

            // only check the bits that have been changed
            if (lastChanges[i] != -1) {
                // bit i has been changed to 0 or 1 => check matrix

                // if lastChanges[i] == 1 => true, else (lastChanges[i] == 0) => false
                boolean bitValue = ((lastChanges[i] == 1) ? true : false);

                // rmx - 1 because rmx sends bus RMX1 as 1
                if (checkByteMatrix == true) {
                    // only once check the matrix.byteMatrix for the given byte of lastChanges
                    result.addAll(traverse(busId - 1, systemadress, i, bitValue, checkByteMatrix));
                    checkByteMatrix = false;
                } else {
                    result.addAll(traverse(busId - 1, systemadress, i, bitValue, checkByteMatrix));
                }

            } // end if

        } // end for

        return result;
    }

    /*-----------------------------------------------------------------------------------------------
      HELPER METHODS
      - traverseBitAndByteMatrixAndCheck()
      - traverseBitMatrixAndCheck()
     ----------------------------------------------------------------------------------------------*/

    /**
     * traverses the matrix, checks if any conditions are true and returns the commulated actionsequences
     * <p>
     * logic of ByteMatrix traversal
     * - intuitiv: to the right until I hit myself then downwards<br>
     * - the traversal of the BitMatrix and ByteMatrix happens simultaneously<br>
     * - at the last bit of each byte a rule containing the given systemadress is checked<br>
     * - simultaneously to checking we move in the ByteMatrix one field to the right (row traversal) or
     * one field down (column traversal)<br>
     *
     * @param busId                 includes bus id of RMX bus -1
     * @param systemadress          index of the systemadrese in which bit that has changed
     * @param systemadress_bitIndex index of the bit that has changed
     * @param bitValue              value to which the bit has changed
     * @param checkByteMatrix       indicates whether the ByteMatrix needs to be checked
     */
    private List<ActionSequence> traverse(int busId, int systemadress, int systemadress_bitIndex, boolean bitValue, boolean checkByteMatrix) {

        /* ------------------BYTE-MATRIX-------------------*/
        // currentByte Value of the given systemadress of the given bus
        int currentByte = busDepot.getBus(busId + 1).getCurrentByte(systemadress);
        /* -----------------------------------------------*/

        // contains ActionSequences that are triggered
        List<ActionSequence> result = new ArrayList<>();

        // calculate the bitIndex of the given bit in the given systemadress and bus
        int bitIndexChangedBit = MatrixCalcUtil.calcBitIndex(busId, systemadress, systemadress_bitIndex);


        // calculate the first field in the BitMatrix to start traversal
        int fieldIndexBitMatrix = MatrixCalcUtil.calcGauss(bitIndexChangedBit);

        /* ------------------BYTE-MATRIX-------------------*/
        // calculate the byteIndex of the given systemadress
        int byteIndexChangedBit = bitIndexChangedBit / 8;

        // calculate the first field in the ByteMatrix to start traversal
        int fieldIndexByteMatrix = MatrixCalcUtil.calcGauss(byteIndexChangedBit);
        /* -----------------------------------------------*/

        Bus currentBus = null;

        // loop for traversal
        for (int i = 0; i < (Constants.NUMBER_BITS_PER_BUS * Constants.NUMBER_OF_BUSSES); i++) {

            // updates the bus if the bitIndexColumn is in the next higher bus
            Bus tempBus;
            currentBus = ((tempBus = MatrixUtil.getNextHigherBusBitMatrix(i)) != null) ? tempBus : currentBus;

            ActionSequence actionSequence = null;

            if (i < bitIndexChangedBit) {
                // Row-Traversal
                // get Variables needed for check
                int systemadress_bitIndexRow = systemadress;
                boolean bitValueRow = bitValue;

                int systemadress_bitIndexColumn = MatrixCalcUtil.getSystemadressByBitIndex(i);
                boolean bitValueColumn = currentBus.isBitSet(systemadress_bitIndexColumn, (i % 8));

                if (bitMatrix.getBitMatrixField(fieldIndexBitMatrix) != null) {
                    // an ActionSequenceWrapper is in the field

                    // returns the actionSequence by the current state (00, 10, 01 or 11)
                    actionSequence = MatrixUtil.checkBitMatrixField(bitValueRow,
                            bitValueColumn, bitMatrix.getBitMatrixField(fieldIndexBitMatrix));
                }

                // move to the right in the row of the BitMatrix
                fieldIndexBitMatrix++;

                /* ------------------BYTE-MATRIX-------------------*/
                // we are at the last index of the current byte
                if (i % 8 == 7 && checkByteMatrix) {

                    int byteValueRow = currentByte;
                    int byteValueColumn = currentBus.getCurrentByte(systemadress_bitIndexColumn);

                    // check field of the ByteMatrix for rule including the states of the recordByte and currentByte
                    ActionSequence actionSequenceByteMatrix = MatrixUtil.checkByteMatrixField(byteValueRow,
                            byteValueColumn, byteMatrix.getByteMatrixField(fieldIndexByteMatrix));

                    if (actionSequenceByteMatrix != null) {
                        // ActionSequence for point exists = a rule has been defined
                        result.add(actionSequenceByteMatrix);
                    }

                    System.out.println("R: " + fieldIndexByteMatrix);

                    // move in the byteMatrix one field to the right
                    fieldIndexByteMatrix++;
                }
                /* -----------------------------------------------*/

            } else {
                // Column-Traversal -> mirroring the bitIndices

                // get Variables needed for check
                int systemadress_bitIndexRow = MatrixCalcUtil.getSystemadressByBitIndex(i);
                boolean bitValueRow = currentBus.isBitSet(systemadress_bitIndexRow, (i % 8));

                int systemadress_bitIndexColumn = systemadress;
                boolean bitValueColumn = bitValue;

                if (bitMatrix.getBitMatrixField(fieldIndexBitMatrix) != null) {
                    // an ActionSequenceWrapper is in the field

                    // returns the actionSequence by the current state (00, 10, 01 or 11)
                    actionSequence = MatrixUtil.checkBitMatrixField(bitValueRow,
                            bitValueColumn, bitMatrix.getBitMatrixField(fieldIndexBitMatrix));
                }

                // move bitindex one field down => i+1 (in column traversal ist i = bitIndexRow)
                fieldIndexBitMatrix = MatrixCalcUtil.calcGauss(i + 1) + bitIndexChangedBit;

                /* ------------------BYTE-MATRIX-------------------*/
                // we are at the last index of the current byte
                if (i % 8 == 7 && checkByteMatrix) {

                    int byteValueRow = currentBus.getCurrentByte(systemadress_bitIndexRow);
                    int byteValueColumn = currentByte;

                    // check field of the ByteMatrix for rule including the states of the recordByte and currentByte
                    ActionSequence actionSequenceByteMatrix = MatrixUtil.checkByteMatrixField(byteValueColumn,
                            byteValueRow, byteMatrix.getByteMatrixField(fieldIndexByteMatrix));

                    if (actionSequenceByteMatrix != null) {
                        // ActionSequence for point exists = a rule has been defined
                        result.add(actionSequenceByteMatrix);
                    }

                    System.out.println("C: " + fieldIndexByteMatrix);

                    // move in the byteMatrix one field down
                    fieldIndexByteMatrix = MatrixCalcUtil.calcGauss(systemadress_bitIndexRow+1) + byteIndexChangedBit;
                }
                /* -----------------------------------------------*/
            }

            if (actionSequence != null) {
                // ActionSequence for point exists => a rule has been defined
                result.add(actionSequence);
            }

        }

        // return triggered ActionSequences
        return result;
    }

}
