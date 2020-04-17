package matrix;

import action.actionSequence.ActionSequence;
import bus.Bus;
import bus.BusDepot;
import matrix.bitMatrix.BitMatrix;
import matrix.byteMatrix.ByteMatrix;
import utilities.ByteUtil;
import utilities.Constants;

import java.util.ArrayList;
import java.util.List;

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


    private BitMatrix bitMatrix;

    private ByteMatrix byteMatrix;

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
                    ActionSequence actionSequence = MatrixUtil.getActionSequenceByState(systemadress_bitIndexRow, bitValueRow,
                            systemadress_bitIndexColumn, bitValueColumn, bitMatrix.getBitMatrixField(fieldIndexBitMatrix));

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

                // determine which has the smaller byteIndex
                if(byteIndexRow < byteIndexColumn) {
                    byteValueSmall = currentBusRow.getCurrentByte(byteIndexRow);
                    byteValueBig = currentBusColumn.getCurrentByte(byteIndexColumn);
                } else {
                    byteValueSmall = currentBusColumn.getCurrentByte(byteIndexColumn);
                    byteValueBig = currentBusRow.getCurrentByte(byteIndexRow);
                }

                ActionSequence actionSequenceByteMatrix = checkByteMatrixField(byteValueSmall, byteValueBig, fieldIndexByteMatrix);

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
     * in a list. The List is empty if no ActionSequences have been triggered.
     *
     * format of lastChanges (index 0 to 7)
     * - 1: bit changed to 1
     * - 0: bit changed to 0
     * - -1: nothing changed
     *
     * @param busId the busId of the bus the changes
     * @param systemadress the systemadress of the changes
     * @param lastChanges the changes of the given systemadress
     * @return List of ActionSequences that have been triggered, the list is empty if no ActionSequences have been triggered
     */
    public List<ActionSequence> check(int busId, int systemadress, Integer[] lastChanges) {

        List<ActionSequence> result = new ArrayList<>();

        boolean checkedByteMatrix = false;

        // iterates over "bits" of lastChanged
        for (int i = 0; i < 8; i++) {
            // only check the bits that have been changed
            if (lastChanges[i] != -1) {
                // bit i has been changed => check matrix

                // if lastChanges[i] == 1 => true, else (lastChanges[i] == 0) => false
                boolean bitValue = ((lastChanges[i] == 1) ? true : false);

                // rmx - 1 because rmx sends busRMX1 as 1
                if(checkedByteMatrix == false) {
                    // only once check the matrix.byteMatrix for the given byte
                    result.addAll(traverseBitAndByteMatrixAndCheck(busId - 1, systemadress, i, bitValue));
                    checkedByteMatrix = true;
                } else {
                    result.addAll(traverseBitMatrixAndCheck(busId - 1, systemadress, i, bitValue));
                }

            }

        }

        return result;
    }

    /*-----------------------------------------------------------------------------------------------
      HELPER METHODS
      - traverseBitAndByteMatrixAndCheck()
      - traverseBitMatrixAndCheck()
     ----------------------------------------------------------------------------------------------*/

    /**
     * traverses the matrix, checks if any conditions are true and returns the commulated actionsequences
     *
     * logic of ByteMatrix traversal
     * - the traversal happens simultaneously to the traversal of the BitMatrix
     * - through traversal the state of each byte is recorded
     * - after one byte is recorded fully check of exisiting rule containing the currentByte and  last recorded Byte
     * - simultaneously we move in the ByteMatrix one field to the right (recording is done in the row loop) or
     *      one field down (recorded is done in the column loop)
     * - since the check of a byte happens after fully recording each bit (which means they have been traversed in the
     *      BitMatrix) we have to check the last recording outside of the loops, since the loops only traverse to bits
     *      inside the bitMatrix
     *
     * @param busId                 includes bus id of RMX bus -1
     * @param systemadress          index of the systemadrese in which bit that has changed
     * @param systemadress_bitIndex index of the bit that has changed
     * @param bitValue              value to which the bit has changed
     */
    private List<ActionSequence> traverseBitAndByteMatrixAndCheck(int busId, int systemadress, int systemadress_bitIndex,
                                                                  boolean bitValue) {

        // currentByte Value of the given systemadress of the given bus
        int currentByte = busDepot.getBus(busId+1).getCurrentByte(systemadress);

        // init recordedByte
        // always includes the last 8 traversed bit values
        Integer[] recordByte = new Integer[8];

        List<ActionSequence> result = new ArrayList<>();

        /*
            ROW TRAVERSAL --------------------------------------------------------------------------
         */

        /*
        Bit Matrix
         */
        int bitIndexChangedBit = MatrixCalcUtil.calcBitIndex(busId, systemadress, systemadress_bitIndex);
        // get index of first field to check
        int fieldIndexBitMatrix = MatrixCalcUtil.calcGauss(bitIndexChangedBit);

        /*
        Byte Matrix
         */
        //start row bit index of the byte matrix
        int byteIndexChangedBit = bitIndexChangedBit/8;
        //get index of first field in byte matrix
        int fieldIndexByteMatrix = MatrixCalcUtil.calcGauss(byteIndexChangedBit);

        //counter of who many fields i moved in the matrix
        int counterBitMatrix = 0;

        Bus currentBusColumn = null;

        // indicates if the for loop is in its first iteration
        boolean firstTime = true;

        // loop through entire row to the right
        for (int bitIndexColumn = 0; bitIndexColumn <= bitIndexChangedBit; bitIndexColumn++) {

            // updates the bus if the bitIndexColumn is in the next higher bus
            Bus tempBusColumn;
            currentBusColumn = ((tempBusColumn = MatrixUtil.getNextHigherBusBitMatrix(bitIndexColumn)) != null) ? tempBusColumn : currentBusColumn;

            // get BitValue of bitIndexColumn (needs to be recorded for the matrix.byteMatrix)
            int systemadress_bitIndexColumn = MatrixCalcUtil.getSystemadressByBitIndex(bitIndexColumn);
            boolean bitValueColumn = currentBusColumn.isBitSet(systemadress_bitIndexColumn, (bitIndexColumn % 8));

            if (bitMatrix.getBitMatrixField(fieldIndexBitMatrix) != null) {
                // an ActionSequenceWrapper is in the field

                // get Variables needed for check
                int systemadress_bitIndexRow = systemadress;
                boolean bitValueRow = bitValue;

                // returns the actionSequence by the current state (00, 10, 01 or 11)
                ActionSequence actionSequenceBitMatrix = MatrixUtil.getActionSequenceByState(systemadress_bitIndexRow, bitValueRow,
                        systemadress_bitIndexColumn, bitValueColumn, bitMatrix.getBitMatrixField(fieldIndexBitMatrix));

                if (actionSequenceBitMatrix != null) {
                    // ActionSequence for point exists = a rule has been defined
                    result.add(actionSequenceBitMatrix);
                }

            }

            fieldIndexBitMatrix++; // move to the right in the row

            /*
        Byte Matrix
         */
            // we enterted the next byte, we are at the first index of the next byte
            // dont go in in the first iteration since no bits have been recorded
            if(counterBitMatrix % 8 == 0 && firstTime == false) {

                // check field of the ByteMatrix for rule including the states of the recordByte and currentByte
                ActionSequence actionSequenceByteMatrix = checkByteMatrixField(ByteUtil.getByteByByteArray(recordByte), currentByte, fieldIndexByteMatrix);

                if (actionSequenceByteMatrix != null) {
                    // ActionSequence for point exists = a rule has been defined
                    result.add(actionSequenceByteMatrix);
                }


                //move in the byte matrix one field to the right
                fieldIndexByteMatrix++;
            }

            // record bitValue for the ByteMatrix
            recordByte[counterBitMatrix%8] = (bitValueColumn) ? 1 : 0; // if true -> 1, false -> 0

            //because i moved one field to the right
            counterBitMatrix++;

            // after the first iteration = false
            firstTime = false;
        }

        /*
            COLUMN TRAVERSAL --------------------------------------------------------------------------
         */

        // bus of the row (initially given bus by the method)
        Bus currentBusRow = busDepot.getBus(busId + 1); // +1 da in busdepot nach der Busid (nach RMX) gespeichert sind

        // bitIndexRow starts at the bitIndex
        int bitIndexRow = bitIndexChangedBit + 1; // start column traversal one row below
        fieldIndexBitMatrix = MatrixCalcUtil.calcGauss(bitIndexRow) + bitIndexChangedBit; // move one field down

        /*
        Byte Matrix
         */
        // bitIndexRow starts at the bitIndex
        int byteIndexRow = byteIndexChangedBit + 1; // start column traversal one row below

        // need to save the lastField of the ByteMatrix before update since I have to check the last recorded byte in the end
        // with the field of the ByteMatrix it corresponds to
        int lastFieldByteMatrix = fieldIndexByteMatrix;

        // loop until field is outside of the triangular matrix
        while (fieldIndexBitMatrix < BitMatrix.bitMatrixArraySize) {

            // updates the bus if the bitIndexRow is in the next higher bus
            Bus tempBusRow;
            currentBusRow = ((tempBusRow = MatrixUtil.getNextHigherBusBitMatrix(bitIndexRow)) != null) ? tempBusRow : currentBusRow;

            // get BitValue of bitIndexRow (needs to be recorded for the matrix.byteMatrix)
            int systemadress_bitIndexRow = MatrixCalcUtil.getSystemadressByBitIndex(bitIndexRow);
            boolean bitValueRow = currentBusRow.isBitSet(systemadress_bitIndexRow, (bitIndexRow % 8));

            if ((bitMatrix.getBitMatrixField(fieldIndexBitMatrix) != null)) {
                // an ActionSequenceWrapper is in the field

                // get Variables needed for check
                int systemadress_bitIndexColumn = systemadress;
                boolean bitValueColumn = bitValue;

                // returns the actionSequence by the current state (00, 10, 01 or 11)
                ActionSequence actionSequence = MatrixUtil.getActionSequenceByState(systemadress_bitIndexRow, bitValueRow,
                        systemadress_bitIndexColumn, bitValueColumn, bitMatrix.getBitMatrixField(fieldIndexBitMatrix));

                if (actionSequence != null) {
                    // ActionSequence for point exists = a rule has been defined
                    result.add(actionSequence);
                }
            }

            bitIndexRow++; // move bitindex one field down
            fieldIndexBitMatrix = MatrixCalcUtil.calcGauss(bitIndexRow) + bitIndexChangedBit; // move one field down

                /*
                Byte Matrix
              */
            // we enterted the next byte, we are at the first index of the next byte
            if(counterBitMatrix % 8 == 0) {

                ActionSequence actionSequenceByteMatrix = checkByteMatrixField(currentByte, ByteUtil.getByteByByteArray(recordByte), fieldIndexByteMatrix);

                if (actionSequenceByteMatrix != null) {
                    // ActionSequence for point exists = a rule has been defined
                    result.add(actionSequenceByteMatrix);
                }

                //move in the byte matrix one field down
                byteIndexRow++;
                lastFieldByteMatrix = fieldIndexByteMatrix;
                fieldIndexByteMatrix = MatrixCalcUtil.calcGauss(byteIndexRow) + byteIndexChangedBit;
            }

            // record bitValue for the ByteMatrix
            recordByte[counterBitMatrix%8] = (bitValueRow) ? 1 : 0; // if true -> 1, false -> 0

            //because i moved one field to the right
            counterBitMatrix++;

        } // while

        // check last field of matrix.byteMatrix
        ActionSequence actionSequenceByteMatrix = checkByteMatrixField(currentByte, ByteUtil.getByteByByteArray(recordByte), lastFieldByteMatrix);

        if (actionSequenceByteMatrix != null) {
            // ActionSequence for point exists = a rule has been defined
            result.add(actionSequenceByteMatrix);
        }

        // return ActionSequences of true conditions
        return result;
    }


    /*
        BITMATRIX
     */

    /**
     * traverses the matrix, checks if any conditions are true and returns the commulated actionsequences
     *
     * @param busId                 includes bus id of RMX bus -1
     * @param systemadress          index of the systemadrese in which bit that has changed
     * @param systemadress_bitIndex index of the bit that has changed
     * @param bitValue              value to which the bit has changed
     */
    private List<ActionSequence> traverseBitMatrixAndCheck(int busId, int systemadress, int systemadress_bitIndex, boolean bitValue) {

        List<ActionSequence> result = new ArrayList<>();

        /*
            ROW TRAVERSAL --------------------------------------------------------------------------
         */

        /*
        Bit Matrix
         */
        int bitIndexChangedBit = MatrixCalcUtil.calcBitIndex(busId, systemadress, systemadress_bitIndex);
        // get index of first field to check
        int fieldIndexBitMatrix = MatrixCalcUtil.calcGauss(bitIndexChangedBit);

        Bus currentBusColumn = null;

        // loop through entire row to the right
        for (int bitIndexColumn = 0; bitIndexColumn <= bitIndexChangedBit; bitIndexColumn++) {

            // updates the bus if the bitIndexColumn is in the next higher bus
            Bus tempBusColumn;
            currentBusColumn = ((tempBusColumn = MatrixUtil.getNextHigherBusBitMatrix(bitIndexColumn)) != null) ? tempBusColumn : currentBusColumn;

            if (bitMatrix.getBitMatrixField(fieldIndexBitMatrix) != null) {
                // an ActionSequenceWrapper is in the field

                // get Variables needed for check
                int systemadress_bitIndexRow = systemadress;
                int systemadress_bitIndexColumn = MatrixCalcUtil.getSystemadressByBitIndex(bitIndexColumn);
                boolean bitValueRow = bitValue;
                boolean bitValueColumn = currentBusColumn.isBitSet(systemadress_bitIndexColumn, (bitIndexColumn % 8));

                // returns the actionSequence by the current state (00, 10, 01 or 11)
                ActionSequence actionSequence = MatrixUtil.getActionSequenceByState(systemadress_bitIndexRow, bitValueRow,
                        systemadress_bitIndexColumn, bitValueColumn, bitMatrix.getBitMatrixField(fieldIndexBitMatrix));

                if (actionSequence != null) {
                    // ActionSequence for point exists = a rule has been defined
                    result.add(actionSequence);
                }

            }

            fieldIndexBitMatrix++; // move to the right in the row

        }

        /*
            COLUMN TRAVERSAL --------------------------------------------------------------------------
         */

        // bus of the row (initially given bus by the method)
        Bus currentBusRow = busDepot.getBus(busId + 1); // +1 da in busdepot nach der Busid (nach RMX) gespeichert sind

        // bitIndexRow starts at the bitIndex
        int bitIndexRow = bitIndexChangedBit + 1; // start column traversal one row below
        fieldIndexBitMatrix = MatrixCalcUtil.calcGauss(bitIndexRow) + bitIndexChangedBit; // move one field down

        // loop until field is outside of the triangular matrix
        while (fieldIndexBitMatrix < BitMatrix.bitMatrixArraySize) {

            // updates the bus if the bitIndexRow is in the next higher bus
            Bus tempBusRow;
            currentBusRow = ((tempBusRow = MatrixUtil.getNextHigherBusBitMatrix(bitIndexRow)) != null) ? tempBusRow : currentBusRow;

            if ((bitMatrix.getBitMatrixField(fieldIndexBitMatrix) != null)) {
                // an ActionSequenceWrapper is in the field

                // get Variables needed for check
                int systemadress_bitIndexRow = MatrixCalcUtil.getSystemadressByBitIndex(bitIndexRow);
                int systemadress_bitIndexColumn = systemadress;
                boolean bitValueRow = currentBusRow.isBitSet(systemadress_bitIndexRow, (bitIndexRow % 8));
                boolean bitValueColumn = bitValue;

                // returns the actionSequence by the current state (00, 10, 01 or 11)
                ActionSequence actionSequence = MatrixUtil.getActionSequenceByState(systemadress_bitIndexRow, bitValueRow,
                        systemadress_bitIndexColumn, bitValueColumn, bitMatrix.getBitMatrixField(fieldIndexBitMatrix));

                if (actionSequence != null) {
                    // ActionSequence for point exists = a rule has been defined
                    result.add(actionSequence);
                }
            }

            bitIndexRow++; // move bitindex one field down
            fieldIndexBitMatrix = MatrixCalcUtil.calcGauss(bitIndexRow) + bitIndexChangedBit; // move one field down

        }

        // return ActionSequences of true conditions
        return result;
    }


    /*
     * BYTE MATRIX
     */
    public ActionSequence checkByteMatrixField(int byteValueSmall, int byteValueBig, int fieldIndexByteMatrix) {

        ActionSequence result = null;

        if(byteMatrix.getByteMatrixField(fieldIndexByteMatrix) != null) {
            result = byteMatrix.getByteMatrixField(fieldIndexByteMatrix).getActionSequenceByState(byteValueSmall, byteValueBig);
        }

        return result;
    }


}
