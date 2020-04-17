package matrix.bitMatrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import utilities.ByteUtil;
import utilities.Constants;
import action.actionSequence.ActionSequence;
import action.actionSequence.ActionSequenceWrapper;
import bus.Bus;
import bus.BusDepot;
import matrix.byteMatrix.ByteMatrix;
import matrix.MatrixUtil;

/**
 * Class that represents the matrix for checking the rules
 *
 * @author Team SmartRMX
 */
public class BitMatrix {

    // Singleton-Pattern START -----------------------------------------

    /**
     * Singleton instance of Matrix
     */
    private static BitMatrix instance = null;

    /**
     * private constructor to prevent instantiation
     */
    private BitMatrix() {

    }

    /**
     * Returns singleton Matrix instance
     *
     * @return Matrix Singleton instance
     */
    public static synchronized BitMatrix getMatrix() {
        if (instance == null) {
            instance = new BitMatrix();
            instance.matrix = new ActionSequenceWrapper[arraySize];
            instance.busDepot = BusDepot.getBusDepot();
            instance.byteMatrix = ByteMatrix.getMatrix();

        }

        return instance;
    }

    // Singleton-Pattern END ________________________________________________

    /**
     * The matrix is represented as triangular matrix
     * number of fields of an triangular matrix (symmetrical) with n elements: (( n (n + 1)) / 2)
     */
    private static int arraySize = MatrixUtil.calcGauss(Constants.NUMBER_BITS_PER_BUS); // = 401.856

    /**
     * array holding all ActionSequenceWrapper of the matrix
     */
    private ActionSequenceWrapper[] matrix;

    /**
     * busDepot handling the access to the different busses
     */
    private BusDepot busDepot;

    private ByteMatrix byteMatrix;


    /*-----------------------------------------------------------------------------------------------
      METHODS FOR CHECKING
      - checkAllFields
      - check
      - traverseMatrixAndCheck
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
        int field = 0;
        // outer for loop goes along the row
        for (int bitIndexRow = 0; bitIndexRow < (Constants.NUMBER_OF_BUSSES * Constants.NUMBER_BITS_PER_BUS); bitIndexRow++) {

            // updates the bus if the bitIndexRow is in the next higher bus
            Bus tempBusRow;
            currentBusRow = ((tempBusRow = getNextHigherBusBitMatrix(bitIndexRow)) != null) ? tempBusRow : currentBusRow;

            //inner for loop goes along the column
            for (int bitIndexColumn = 0; bitIndexColumn < bitIndexRow + 1; bitIndexColumn++) {
                // only check conditions if a ActionSequenceWrapper is in the matrix (= a rule is defined)

                // updates the bus if the bitIndexColumn is in the next higher bus
                Bus tempBusColumn;
                currentBusColumn = ((tempBusColumn = getNextHigherBusBitMatrix(bitIndexColumn)) != null) ? tempBusColumn : currentBusColumn;

                if (matrix[field] != null) {
                    // an ActionSequenceWrapper is in the field

                    // get Variables needed for check
                    int systemadress_bitIndexRow = MatrixUtil.getSystemadressByBitIndex(bitIndexRow);
                    int systemadress_bitIndexColumn = MatrixUtil.getSystemadressByBitIndex(bitIndexColumn);
                    boolean bitValueRow = currentBusRow.isBitSet(systemadress_bitIndexRow, (bitIndexRow % 8));
                    boolean bitValueColumn = currentBusColumn.isBitSet(systemadress_bitIndexColumn, (bitIndexColumn % 8));

                    // returns the actionSequence by the current state (00, 10, 01 or 11)
                    ActionSequence actionSequence = getActionSequenceByState(systemadress_bitIndexRow, bitValueRow, systemadress_bitIndexColumn, bitValueColumn, field);

                    if (actionSequence != null) {
                        // ActionSequence for point exists = a rule has been defined
                        result.add(actionSequence);
                    }

                }

                field++; // go to the next field

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
        int field = 0;
        // outer for loop goes along the row
        for (int byteIndexRow = 0; byteIndexRow < (Constants.NUMBER_OF_BUSSES * Constants.NUMBER_SYSTEMADRESSES_PER_BUS); byteIndexRow++) {

            // updates the bus if the byteIndexRow is in the next higher bus
            Bus tempBusRow;
            currentBusRow = ((tempBusRow = getNextHigherBusByteMatrix(byteIndexRow)) != null) ? tempBusRow : currentBusRow;

            //inner for loop goes along the column
            for (int byteIndexColumn = 0; byteIndexColumn < byteIndexRow + 1; byteIndexColumn++) {
                // only check conditions if a ByteRuleWrapper is in the matrix (= a rule is defined)

                // updates the bus if the byteIndexColumn is in the next higher bus
                Bus tempBusColumn;
                currentBusColumn = ((tempBusColumn = getNextHigherBusByteMatrix(byteIndexColumn)) != null) ? tempBusColumn : currentBusColumn;


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

                ActionSequence actionSequenceByteMatrix = byteMatrix.checkField(byteValueSmall,byteValueBig, field);

                if (actionSequenceByteMatrix != null) {
                    // ActionSequence for point exists = a rule has been defined
                    result.add(actionSequenceByteMatrix);
                }

                field++; // go to the next field

            } // inner for-loop
        } // outer for-loop

        return result;
    }

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

                System.out.println("LAST CHANGES: " + Arrays.toString(lastChanges));
                System.out.println("INDEX OF LAST CHANGES: " + i);
                // rmx - 1 because rmx sends busRMX1 as 1
                if(checkedByteMatrix == false) {
                    // only once check the matrix.byteMatrix for the given byte
                    System.out.println("ICH BIN IM DURCHLAUF MIT DER BYTEMATRIX");
                    result.addAll(traverseBitAndByteMatrixAndCheck(busId - 1, systemadress, i, bitValue));
                    checkedByteMatrix = true;
                } else {
                    result.addAll(traverseBitMatrixAndCheck(busId - 1, systemadress, i, bitValue));
                }

            }

        }

        return result;
    }

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
    private List<ActionSequence> traverseBitAndByteMatrixAndCheck(int busId, int systemadress, int systemadress_bitIndex, boolean bitValue) {

        // currentByte Value of the given systemadress of the given bus
        int currentByte = busDepot.getBus(busId+1).getCurrentByte(systemadress);

        // init recordedByte
        // always includes the last 8 traversed bit values
        Integer[] recordByte = new Integer[8];

        List<ActionSequence> result = new ArrayList<>();

        /*
            ROW TRAVERSAL --------------------------------------------------------------------------
         */
        System.out.println("------------- row ---------------");

        /*
        Bit Matrix
         */
        int bitIndexChangedBit = MatrixUtil.calcBitIndex(busId, systemadress, systemadress_bitIndex);
        // get index of first field to check
        int fieldBitMatrix = MatrixUtil.calcGauss(bitIndexChangedBit);

        /*
        Byte Matrix
         */
        //start row bit index of the byte matrix
        int byteIndexChangedBit = bitIndexChangedBit/8;
        //get index of first field in byte matrix
        int fieldByteMatrix = MatrixUtil.calcGauss(byteIndexChangedBit);

        //counter of who many fields i moved in the matrix
        int counter = 0;

        Bus currentBusColumn = null;

        // indicates if the for loop is in its first iteration
        boolean firstTime = true;

        //TODO zustand aufnaheme für jeweils zwei bytes, das aktuelle und das davor

        System.out.println("BITINDEXchangedbit: " + bitIndexChangedBit);
        // loop through entire row to the right
        for (int bitIndexColumn = 0; bitIndexColumn <= bitIndexChangedBit; bitIndexColumn++) {

            System.out.println("Counter: " + counter);

            // updates the bus if the bitIndexColumn is in the next higher bus
            Bus tempBusColumn;
            currentBusColumn = ((tempBusColumn = getNextHigherBusBitMatrix(bitIndexColumn)) != null) ? tempBusColumn : currentBusColumn;

            // get BitValue of bitIndexColumn (needs to be recorded for the matrix.byteMatrix)
            int systemadress_bitIndexColumn = MatrixUtil.getSystemadressByBitIndex(bitIndexColumn);
            boolean bitValueColumn = currentBusColumn.isBitSet(systemadress_bitIndexColumn, (bitIndexColumn % 8));

            if (matrix[fieldBitMatrix] != null) {
                // an ActionSequenceWrapper is in the field

                // get Variables needed for check
                int systemadress_bitIndexRow = systemadress;
                boolean bitValueRow = bitValue;

                // returns the actionSequence by the current state (00, 10, 01 or 11)
                ActionSequence actionSequenceBitMatrix = getActionSequenceByState(systemadress_bitIndexRow, bitValueRow, systemadress_bitIndexColumn, bitValueColumn, fieldBitMatrix);

                if (actionSequenceBitMatrix != null) {
                    // ActionSequence for point exists = a rule has been defined
                    result.add(actionSequenceBitMatrix);
                }

            }

            fieldBitMatrix++; // move to the right in the row

            /*
        Byte Matrix
         */
            // we enterted the next byte, we are at the first index of the next byte
            // dont go in in the first iteration since no bits have been recorded
            if(counter % 8 == 0 && firstTime == false) {

                System.out.println("recorded:" + Arrays.toString(recordByte));
                System.out.println("current:" + currentByte);

                System.out.println("FELD BYTE MATRIX " + fieldByteMatrix);

                // check field of the ByteMatrix for rule including the states of the recordByte and currentByte
                ActionSequence actionSequenceByteMatrix = byteMatrix.checkField(ByteUtil.getByteByByteArray(recordByte), currentByte, fieldByteMatrix);
                System.out.println("ACTIONSEQUENZ: " + actionSequenceByteMatrix);

                if (actionSequenceByteMatrix != null) {
                    // ActionSequence for point exists = a rule has been defined
                    result.add(actionSequenceByteMatrix);
                }


                //move in the byte matrix one field to the right
                fieldByteMatrix++;
            }

            // record bitValue for the ByteMatrix
            recordByte[counter%8] = (bitValueColumn) ? 1 : 0; // if true -> 1, false -> 0

            //because i moved one field to the right
            counter++;

            // after the first iteration = false
            firstTime = false;
        }

        /*
            COLUMN TRAVERSAL --------------------------------------------------------------------------
         */
        System.out.println("------------- column ---------------");

        // bus of the row (initially given bus by the method)
        Bus currentBusRow = busDepot.getBus(busId + 1); // +1 da in busdepot nach der Busid (nach RMX) gespeichert sind

        // bitIndexRow starts at the bitIndex
        int bitIndexRow = bitIndexChangedBit + 1; // start column traversal one row below
        fieldBitMatrix = MatrixUtil.calcGauss(bitIndexRow) + bitIndexChangedBit; // move one field down

        /*
        Byte Matrix
         */
        // bitIndexRow starts at the bitIndex
        int byteIndexRow = byteIndexChangedBit + 1; // start column traversal one row below

        // need to save the lastField of the ByteMatrix before update since I have to check the last recorded byte in the end
        // with the field of the ByteMatrix it corresponds to
        int lastFieldByteMatrix = fieldByteMatrix;

        // loop until field is outside of the triangular matrix
        while (fieldBitMatrix < arraySize) {

            System.out.println("COUNTER: " + counter);

            // updates the bus if the bitIndexRow is in the next higher bus
            Bus tempBusRow;
            currentBusRow = ((tempBusRow = getNextHigherBusBitMatrix(bitIndexRow)) != null) ? tempBusRow : currentBusRow;

            // get BitValue of bitIndexRow (needs to be recorded for the matrix.byteMatrix)
            int systemadress_bitIndexRow = MatrixUtil.getSystemadressByBitIndex(bitIndexRow);
            boolean bitValueRow = currentBusRow.isBitSet(systemadress_bitIndexRow, (bitIndexRow % 8));

            if ((matrix[fieldBitMatrix] != null)) {
                // an ActionSequenceWrapper is in the field

                // get Variables needed for check
                int systemadress_bitIndexColumn = systemadress;
                boolean bitValueColumn = bitValue;

                // returns the actionSequence by the current state (00, 10, 01 or 11)
                ActionSequence actionSequence = getActionSequenceByState(systemadress_bitIndexRow, bitValueRow, systemadress_bitIndexColumn, bitValueColumn, fieldBitMatrix);

                if (actionSequence != null) {
                    // ActionSequence for point exists = a rule has been defined
                    result.add(actionSequence);
                }
            }

            bitIndexRow++; // move bitindex one field down
            fieldBitMatrix = MatrixUtil.calcGauss(bitIndexRow) + bitIndexChangedBit; // move one field down

                /*
                Byte Matrix
              */
            // we enterted the next byte, we are at the first index of the next byte
            if(counter % 8 == 0) {

                System.out.println("ICH PRÜFE FELD BYTE MATRIX " + fieldByteMatrix);
                System.out.println("recordedByte " + Arrays.toString(recordByte));
                System.out.println("currentByte " + currentByte);
                ActionSequence actionSequenceByteMatrix = byteMatrix.checkField(currentByte, ByteUtil.getByteByByteArray(recordByte), fieldByteMatrix);
                System.out.println("ACTIONSEQUENZ: " + actionSequenceByteMatrix);

                if (actionSequenceByteMatrix != null) {
                    // ActionSequence for point exists = a rule has been defined
                    result.add(actionSequenceByteMatrix);
                }

                //move in the byte matrix one field down
                byteIndexRow++;
                lastFieldByteMatrix = fieldByteMatrix;
                fieldByteMatrix = MatrixUtil.calcGauss(byteIndexRow) + byteIndexChangedBit;
            }

            // record bitValue for the ByteMatrix
            recordByte[counter%8] = (bitValueRow) ? 1 : 0; // if true -> 1, false -> 0

            //because i moved one field to the right
            counter++;

        } // while

        // check last field of matrix.byteMatrix
        System.out.println("ICH PRÜFE FELD BYTE MATRIX " + fieldByteMatrix);
        System.out.println(Arrays.toString(recordByte));
        System.out.println(currentByte);
        ActionSequence actionSequenceByteMatrix = byteMatrix.checkField(currentByte, ByteUtil.getByteByByteArray(recordByte), lastFieldByteMatrix);
        System.out.println("ACTIONSEQUENZ: " + actionSequenceByteMatrix);

        if (actionSequenceByteMatrix != null) {
            // ActionSequence for point exists = a rule has been defined
            result.add(actionSequenceByteMatrix);
        }

        // return ActionSequences of true conditions
        return result;
    }

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
        System.out.println("------------- row ---------------");

        /*
        Bit Matrix
         */
        int bitIndexChangedBit = MatrixUtil.calcBitIndex(busId, systemadress, systemadress_bitIndex);
        // get index of first field to check
        int fieldBitMatrix = MatrixUtil.calcGauss(bitIndexChangedBit);


        Bus currentBusColumn = null;

        // loop through entire row to the right
        for (int bitIndexColumn = 0; bitIndexColumn <= bitIndexChangedBit; bitIndexColumn++) {

            // updates the bus if the bitIndexColumn is in the next higher bus
            Bus tempBusColumn;
            currentBusColumn = ((tempBusColumn = getNextHigherBusBitMatrix(bitIndexColumn)) != null) ? tempBusColumn : currentBusColumn;

            System.out.println("ROW-FIELD" + fieldBitMatrix);

            if (matrix[fieldBitMatrix] != null) {
                // an ActionSequenceWrapper is in the field

                // get Variables needed for check
                int systemadress_bitIndexRow = systemadress;
                int systemadress_bitIndexColumn = MatrixUtil.getSystemadressByBitIndex(bitIndexColumn);
                boolean bitValueRow = bitValue;
                boolean bitValueColumn = currentBusColumn.isBitSet(systemadress_bitIndexColumn, (bitIndexColumn % 8));

                // returns the actionSequence by the current state (00, 10, 01 or 11)
                ActionSequence actionSequence = getActionSequenceByState(systemadress_bitIndexRow, bitValueRow, systemadress_bitIndexColumn, bitValueColumn, fieldBitMatrix);

                if (actionSequence != null) {
                    // ActionSequence for point exists = a rule has been defined
                    result.add(actionSequence);
                }

            }

            fieldBitMatrix++; // move to the right in the row

        }

        /*
            COLUMN TRAVERSAL --------------------------------------------------------------------------
         */
        System.out.println("------------- column ---------------");

        // bus of the row (initially given bus by the method)
        Bus currentBusRow = busDepot.getBus(busId + 1); // +1 da in busdepot nach der Busid (nach RMX) gespeichert sind

        // bitIndexRow starts at the bitIndex
        int bitIndexRow = bitIndexChangedBit + 1; // start column traversal one row below
        fieldBitMatrix = MatrixUtil.calcGauss(bitIndexRow) + bitIndexChangedBit; // move one field down

        // loop until field is outside of the triangular matrix
        while (fieldBitMatrix < arraySize) {

            // updates the bus if the bitIndexRow is in the next higher bus
            Bus tempBusRow;
            currentBusRow = ((tempBusRow = getNextHigherBusBitMatrix(bitIndexRow)) != null) ? tempBusRow : currentBusRow;

            System.out.println("COLUMN-FIELD " + fieldBitMatrix);

            if ((matrix[fieldBitMatrix] != null)) {
                // an ActionSequenceWrapper is in the field

                // get Variables needed for check
                int systemadress_bitIndexRow = MatrixUtil.getSystemadressByBitIndex(bitIndexRow);
                int systemadress_bitIndexColumn = systemadress;
                boolean bitValueRow = currentBusRow.isBitSet(systemadress_bitIndexRow, (bitIndexRow % 8));
                boolean bitValueColumn = bitValue;

                // returns the actionSequence by the current state (00, 10, 01 or 11)
                ActionSequence actionSequence = getActionSequenceByState(systemadress_bitIndexRow, bitValueRow, systemadress_bitIndexColumn, bitValueColumn, fieldBitMatrix);

                if (actionSequence != null) {
                    // ActionSequence for point exists = a rule has been defined
                    result.add(actionSequence);
                }
            }

            bitIndexRow++; // move bitindex one field down
            fieldBitMatrix = MatrixUtil.calcGauss(bitIndexRow) + bitIndexChangedBit; // move one field down

        }

        // return ActionSequences of true conditions
        return result;
    }

    /*-----------------------------------------------------------------------------------------------
      HELPER METHODS FOR CHECKING
      - getActionSequenceByState
      - updateBus
     ----------------------------------------------------------------------------------------------*/

    /**
     * Returns (if existend) the ActionSequence of the given field in the matrix specified by the bitIndex of the Row
     * and Column and der corresponding bit values
     * <p>
     * possible combinations of states (row, column): (0,0) - (1,0) - (1,0) - (1,1)
     *
     * @param systemadress_bitIndexRow    systemadress of the bitIndex of the row
     * @param bitValueRow                 bitValue of the bit specified by the bitindex of the row
     * @param systemadress_bitIndexColumn systemadress of the bitIndex of the column
     * @param bitValueColumn              bitValue of the bit specified by the bitindex of the column
     * @param field                       the field to check in the matrix
     * @return the actionSequence of the given state, returns null if no rule has been defined for the given state
     */
    public ActionSequence getActionSequenceByState(int systemadress_bitIndexRow, boolean bitValueRow, int systemadress_bitIndexColumn, boolean bitValueColumn, int field) {

        ActionSequence actionSequence;

        // the other bit is currently set
        if (bitValueColumn) {
            // both conditions are true => get ActionSequence of point in matrix

            if (bitValueRow) {
                // bit value of row index =  1, bit value of column index is 1
                System.out.println("ICH BIN HIER DRIN 1");
                actionSequence = matrix[field].getActionSequence1And1();
            } else {
                // bit value of row index = 0, bit value of column index is 1
                System.out.println("ICH BIN HIER DRIN 2");
                actionSequence = matrix[field].getActionSequence0And1();
            }

        } else {
            // the other bit is not set
            if (bitValueRow) {
                // bit value of row index =  1, bit value of column index is 0
                System.out.println("ICH BIN HIER DRIN 3");
                actionSequence = matrix[field].getActionSequence1And0();
            } else {
                // bit value of row index =  0, bit value of column index is 0
                System.out.println("ICH BIN HIER DRIN 4");
                actionSequence = matrix[field].getActionSequence0And0();
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
    public Bus getNextHigherBusBitMatrix(int bitIndex) {

        Bus bus = null;

        if (bitIndex % Constants.NUMBER_BITS_PER_BUS == 0) {
            // get current bus => + 1 since RMX starts counting at 1
            bus = busDepot.getBus(((bitIndex / Constants.NUMBER_BITS_PER_BUS) + 1));
        }

        return bus;
    }

    /**
     * Updates the given bus to the next higher bus if the byteIndex surpasses the last byte of the last bus.
     * Change happens exactly at the start of the new bus, for other byteIndexes this method returns null
     *
     * @param byteIndex byteIndex to check
     * @return the bus of the given byteindex, null if bitIndex isnt exactly at a "changing point"
     */
    public Bus getNextHigherBusByteMatrix(int byteIndex) {

        Bus bus = null;

        if (byteIndex % Constants.NUMBER_SYSTEMADRESSES_PER_BUS == 0) {
            // get current bus => + 1 since RMX starts counting at 1
            bus = busDepot.getBus(((byteIndex / Constants.NUMBER_SYSTEMADRESSES_PER_BUS) + 1));
        }

        return bus;
    }

    /*-----------------------------------------------------------------------------------------------
      METHODS FOR ADDING ACTIONS TO THE MATRIX
      - addAction
      - addActionSequenceToActionSequenceWrapper
      - addActionSequenceWrapperToMatrix
     ----------------------------------------------------------------------------------------------*/

    /**
     * Method responsible for adding the action sequence to the matrix
     * uses helper methods to add the ActionSequence to the ActionSequenceWrapper and finally into the matrix
     * <p>
     * the passed Integer Arrays need to be two DIFFERENT references
     * <p>
     * format of condition: [bus][systemadresse][bit][bitvalue]
     *
     * @param conditionOne   Integer[] of the first condition
     * @param conditionTwo   Integer[] of the second condition
     * @param actionSequence a actionSequence to add to the matrix
     */
    public void addAction(Integer[] conditionOne, Integer[] conditionTwo, ActionSequence actionSequence) {
        // format [bus][systemadresse][bit]

        System.err.println("conditionOne in addAction: " + Arrays.toString(conditionOne));

        // rmx - 1 because rmx sends busRMX1 as 1
        conditionOne[0] -= conditionOne[0];
        conditionTwo[0] -= conditionTwo[0];

        // calculate bitIndex of both conditions
        int bitIndexConditionOne = MatrixUtil.calcBitIndex(conditionOne[0], conditionOne[1], conditionOne[2]);
        int bitIndexConditionTwo = MatrixUtil.calcBitIndex(conditionTwo[0], conditionTwo[1], conditionTwo[2]);

        // calculation of the pointindex = gaussian value of bigger + bitIndex of smaller
        int pointIndex;

        //check to determine which bit index of the two conditions is bigger
        if (bitIndexConditionOne >= bitIndexConditionTwo) {
            // bit index condition one is bigger
            pointIndex = MatrixUtil.calcGauss(bitIndexConditionOne) + bitIndexConditionTwo;
            addActionSequenceToActionSequenceWrapper(conditionOne, conditionTwo, actionSequence, pointIndex);
        } else {
            // bit index condition one is bigger
            pointIndex = MatrixUtil.calcGauss(bitIndexConditionTwo) + bitIndexConditionOne;
            addActionSequenceToActionSequenceWrapper(conditionTwo, conditionOne, actionSequence, pointIndex);

        }
    }

    /**
     * adds the given action sequence to the ActionWrapper to the right point (index) of the matrix
     *
     * @param conditionBiggerBitIndex  condition with bigger bitindex = row index
     * @param conditionSmallerBitIndex condition with smaller bitindex = column index
     * @param actionSequence           action seq to add
     * @param pointIndex               field in matrix to add action seq wrapper
     */
    private void addActionSequenceToActionSequenceWrapper(Integer[] conditionBiggerBitIndex, Integer[] conditionSmallerBitIndex, ActionSequence actionSequence, int pointIndex) {
        //bit value of bigger bitindex of the conditions = row index
        int bitValue_BiggerBitIndex = conditionBiggerBitIndex[3];
        //bit value  of smaller bitindex of the conditions = column index
        int bitValue_SmallerBitIndex = conditionSmallerBitIndex[3];

        //check wich bit value combination is represented by the given rule (conditions)
        if (bitValue_BiggerBitIndex == 0) {
            if (bitValue_SmallerBitIndex == 0) {
                // bit value row index =  0, bit value column index = 0
                addActionSequenceWrapperToMatrix(0, 0, actionSequence, pointIndex);
            } else {
                //bit value row index = 0 bit value column index =  1
                addActionSequenceWrapperToMatrix(0, 1, actionSequence, pointIndex);
            }
        } else {
            if (bitValue_SmallerBitIndex == 0) {
                // bit value row index = 1, bit value column index =  0
                addActionSequenceWrapperToMatrix(1, 0, actionSequence, pointIndex);
            } else {
                //bit value row index = 1 bit value column index =  1
                addActionSequenceWrapperToMatrix(1, 1, actionSequence, pointIndex);
            }
        }

    }

    /**
     * adds the action sequence to the right action sequence wrapper and adds the action sequence wrapper at right point in matrix
     *
     * @param bitValue_rowIndex    bit value of bigger bitindex of the conditions = row index
     * @param bitValue_columnIndex bit value  of smaller bitindex of the conditions = column index
     * @param actionSequence       action seq to add
     * @param pointIndex           field in matrix to add action seq wrapper
     */
    private void addActionSequenceWrapperToMatrix(int bitValue_rowIndex, int bitValue_columnIndex, ActionSequence actionSequence, int pointIndex) {
        // if no action seq wrapper exists at point add new
        if (matrix[pointIndex] == null) {
            System.out.println("ICH FÜGE EINEN WRAPPER HINZU " + pointIndex);
            matrix[pointIndex] = new ActionSequenceWrapper();
        }

        // set action sequence in the right Action wrapper aat the pointIndex
        matrix[pointIndex].setActionSequence(bitValue_rowIndex, bitValue_columnIndex, actionSequence);
    }
}
