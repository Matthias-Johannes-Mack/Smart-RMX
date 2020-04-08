package matrix;

import java.util.ArrayList;
import java.util.List;

import Utilities.Constants;
import action.ActionSequence;
import action.ActionSequenceWrapper;
import bus.Bus;
import bus.BusDepot;

/**
 * Class for the Matrix
 *
 * @author Team SmartRMX
 */
public class Matrix {

    // Singleton-Pattern START -----------------------------------------

    /**
     * Singleton instance of Matrix
     */
    private static Matrix instance = null;

    /**
     * private constructor to prevent instantiation
     */
    private Matrix() {

    }

    /**
     * Returns singleton Matrix instance
     *
     * @return Matrix Singleton instance
     */
    public static synchronized Matrix getMatrix() {
        if (instance == null) {
            instance = new Matrix();
            instance.matrix = new ActionSequenceWrapper[arraySize];
            instance.busDepot = BusDepot.getBusDepot();
        }

        return instance;
    }

    // Singleton-Pattern END ________________________________________________

    /**
     * The matrix is only saved as triangular matrix
     * number of fields of an triangular matrix (symetrical) with n elements: (( n (n + 1)) / 2)
     */
    private static int arraySize = MatrixUtil.calcGauss(Constants.NUMBER_BITS_PER_BUS); // = 401.856

    /**
     * array holding all ActionSequenceWrapper of the matrix
     */
    private ActionSequenceWrapper[] matrix;

    /**
     * busDepot handling the acess to the different busses
     */
    private BusDepot busDepot;


    //------------------- methods

    /**
     * Method, that checks all fields of the matrix
     * @return
     */
    public List<ActionSequence> checkAllFields() {

        List<ActionSequence> result = new ArrayList<>();

        Bus currentBusRow = null;
        Bus currentBusColumn = null;

        // pointer for the field in the matrix
        int field = 0;
        // outer for loop goes along the row
        for (int bitIndexRow = 0; bitIndexRow < (Constants.NUMBER_OF_BUSSES * Constants.NUMBER_BITS_PER_BUS); bitIndexRow++) {

            // updates the bus if the bitIndex is in the next higher bus
            updateBus(bitIndexRow, currentBusRow);

            //inner for loop goes along the column
            for (int bitIndexColumn = 0; bitIndexColumn < bitIndexRow + 1; bitIndexColumn++) {
                // only check conditions if a ActionSequenceWrapper is in the matrix (= a rule is defined)

                // updates the bus if the bitIndex is in the next higher bus
                updateBus(bitIndexColumn, currentBusColumn);

                if (matrix[field] != null) {

                    int systemadress_bitIndexRow = getSystemadressByBitIndex(bitIndexRow);
                    int systemadress_bitIndexColumn = getSystemadressByBitIndex(bitIndexColumn);

                    boolean bitValueRow = currentBusRow.isBitSet(systemadress_bitIndexRow, (bitIndexRow%8));
                    boolean bitValueColumn = currentBusColumn.isBitSet(systemadress_bitIndexColumn, (bitIndexColumn%8));

                    // returns the actionSequence by the current state (00, 10, 01 or 11)
                    ActionSequence actionSequence = getActionSequenceByState(systemadress_bitIndexRow,bitValueRow, systemadress_bitIndexColumn, bitValueColumn, field);

                    if (actionSequence != null) {
                        // ActionSequence for point exists = a rule has been defined
                        result.add(actionSequence);
                    }

                }

                field++; // go to the next field

            } // inner for
        } // outer for

        return result;
    }

    /**
     * Method if changes happen, called by the schedular Indexs of rmx, adrRMX and
     * lastChanged start at 0!!!
     *
     * @param busId
     * @param systemadress
     * @param lastChanges
     */
    public List<ActionSequence> check(byte busId, byte systemadress, Integer[] lastChanges) {

        List<ActionSequence> result = new ArrayList<>();

        // iterates over "bits" of lastChanged
        for (int i = 0; i < 8; i++) {
            // only check the bits that have been changed
            if (lastChanges[i] != -1) {
                // bit i has been changed => check matrix

                boolean bitValue = ((lastChanges[i] == 1) ? true : false);

                // rmx - 1 because rmx sends busRMX1 as 1
                result.addAll(traverseMatrixAndCheck(busId-1, systemadress, i, bitValue));
            }

        }

        return result;
    }

    /**
     * traverses the matrix, checks if any conditions are true and returns the commulated actionsequences
     *
     * @param busId           includes bus id of RMX bus -1
     * @param systemadress index of the systemadrese in which bit that has changed
     * @param systemadress_bitIndex           index of the bit that has changed
     * @param bitValue      value to which the bit has changed
     */
    public List<ActionSequence> traverseMatrixAndCheck(int busId, int systemadress, int systemadress_bitIndex, boolean bitValue) {

        List<ActionSequence> result = new ArrayList<>();

        // ----------------------------
        // For the row
        // ----------------------------
        System.out.println("------------- row ---------------");

        int bitIndexChangedBit = MatrixUtil.calcBitIndex(busId, systemadress, systemadress_bitIndex);

        // get index of first field to check
        int field = MatrixUtil.calcGauss(bitIndexChangedBit);

        Bus currentBusColumn = null;

        // Bernds-Formel to the right
        // loop through entire row to the right
        for (int bitIndexColumn = 0; bitIndexColumn <= bitIndexChangedBit; bitIndexColumn++) {

            updateBus(bitIndexColumn, currentBusColumn);

            int systemadress_bitIndexRow = systemadress;
            int systemadress_bitIndexColumn = getSystemadressByBitIndex(bitIndexColumn);

            boolean bitValueRow = bitValue;
            boolean bitValueColumn = currentBusColumn.isBitSet(systemadress_bitIndexColumn, (bitIndexColumn%8));


            System.out.println("ROWPOINT-INDEX " + field);

            if (matrix[field] != null) {

                ActionSequence actionSequence = getActionSequenceByState(systemadress_bitIndexRow, bitValueRow, systemadress_bitIndexColumn, bitValueColumn, field);

                if (actionSequence != null) {
                    // ActionSequence for point exists
                    result.add(actionSequence);
                }

            }

            field++; // move to the right in the row
        }

        // ----------------------------
        // For the column
        // ----------------------------
        System.out.println("------------- column ---------------");
        // Gausche' Summenformel (Startpunkt + 1) + startpunkt; startpunkt++ in einer
        // for-loop bis <= max index


        // current bus of rowIndex (initially given bus by method)
        currentBusColumn = busDepot.getBus(busId + 1); // +1 da in busdepot nach der Busid (nach RMX) gespeichert sind

        int oldBitIndex = bitIndexChangedBit;

        bitIndexChangedBit++;
        int columnPointIndex = MatrixUtil.calcGauss(bitIndexChangedBit) + oldBitIndex;

        while (columnPointIndex < arraySize) {

            if (bitIndexChangedBit % Constants.NUMBER_BITS_PER_BUS == 0) {
                System.out.println("I BIM DRIN");
                // get current bus => + 1 since RMX starts counting at 1
                currentBusColumn = busDepot.getBus(((bitIndexChangedBit / Constants.NUMBER_BITS_PER_BUS) + 1));
            }

            // check condition
            // systemadress of current bitIndex that is being checked
            int systemadress_checkedBit = getSystemadressByBitIndex(bitIndexChangedBit);


            System.out.println("COLUMNPOINT-INDEX " + columnPointIndex);

            if ((matrix[columnPointIndex] != null)) {

                ActionSequence actionSequence;
                // the other bit is currently set
                if (currentBusColumn.isBitSet(systemadress_checkedBit, (bitIndexChangedBit%8))) {
                    // both conditions are true => get ActionSequence of point in matrix

                    if (bitValue) {
                        // bit value of row index =  1, bit value of column index is 1+
                        actionSequence = matrix[columnPointIndex].getActionSequence1And1();
                    } else {
                        // bit value of row index =  0, bit value of column index is 1
                        actionSequence = matrix[columnPointIndex].getActionSequence0And1();
                    }

                } else {
                    // the other bit is not set
                    if (bitValue) {
                        // bit value of row index =  1, bit value of column index is 0
                        actionSequence = matrix[columnPointIndex].getActionSequence1And0();
                    } else {
                        // bit value of row index =  0, bit value of column index is 0
                        actionSequence = matrix[columnPointIndex].getActionSequence0And0();
                    }
                }

                if (actionSequence != null) {
                    // ActionSequence for point exists
                    result.add(actionSequence);
                }
            }

            bitIndexChangedBit++;
            columnPointIndex = MatrixUtil.calcGauss(bitIndexChangedBit) + oldBitIndex;
        }

        // return ActionSequences of true conditions
        return result;
    }

    /**
     * Updates the given bus to the next higher bus if the bitIndex surpasses the last bit of the last bus
     *
     * @param bitIndex the bitIndex to check for updating the bus
     * @param currentBus
     */
    public void updateBus(int bitIndex, Bus currentBus) {
        if (bitIndex % Constants.NUMBER_BITS_PER_BUS == 0) {
            // get current bus => + 1 since RMX starts counting at 1
            currentBus = busDepot.getBus(((bitIndex / Constants.NUMBER_BITS_PER_BUS) + 1));
        }
    }

    /**
     * Returns (if existend) the ActionSequence of the given field in the matrix specified by the bitIndex of the Row
     * and Column and der corresponding bit values
     *
     * possible combinations of states (row, column): (0,0) - (1,0) - (1,0) - (1,1)
     *
     * @param systemadress_bitIndexRow systemadress of the bitIndex of the row
     * @param bitValueRow bitValue of the bit specified by the bitindex of the row
     * @param systemadress_bitIndexColumn systemadress of the bitIndex of the column
     * @param bitValueColumn bitValue of the bit specified by the bitindex of the column
     * @param field the field to check in the matrix
     * @return the actionSequence of the given state, returns null if no rule has been defined for the given state
     */
    public ActionSequence getActionSequenceByState(int systemadress_bitIndexRow, boolean bitValueRow, int systemadress_bitIndexColumn, boolean bitValueColumn, int field){

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

    public static int getSystemadressByBitIndex(int bitIndex) {
        return ((bitIndex % Constants.NUMBER_BITS_PER_BUS) / 8); // cuts decimal places
    }


    /**
     * Method responsible for adding the action sequence to the matrix
     * uses helper methods
     * the passed Integer Arrays need to be two DIFFERENT references
     * <p>
     * format of condition: [bus][systemadresse][bit]  TODO [bitvalue]
     *
     * @param conditionOne
     * @param conditionTwo
     * @param actionSequence
     */
    public void addAction(Integer[] conditionOne, Integer[] conditionTwo, ActionSequence actionSequence) {
        // format [bus][systemadresse][bit]

        // rmx - 1 because rmx sends busRMX1 as 1
        conditionOne[0] -= conditionOne[0];
        conditionTwo[0] -= conditionTwo[0];

        // calculate bernd value of both conditions
        int bitIndexConditionOne = MatrixUtil.calcBitIndex(conditionOne[0], conditionOne[1], conditionOne[2]);
        int bitIndexConditionTwo = MatrixUtil.calcBitIndex(conditionTwo[0], conditionTwo[1], conditionTwo[2]);

        // calculation of the pointindex = gaussian value of bigger + bernd of smaller
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
