package matrix.bitMatrix;

import utilities.Constants;
import action.actionSequence.ActionSequence;
import action.actionSequence.ActionSequenceWrapper;

import matrix.MatrixCalcUtil;

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
        bitMatrixArray = new ActionSequenceWrapper[bitMatrixArraySize];
    }

    /**
     * Returns singleton Matrix instance
     *
     * @return Matrix Singleton instance
     */
    public static synchronized BitMatrix getMatrix() {
        if (instance == null) {
            instance = new BitMatrix();
        }

        return instance;
    }

    // Singleton-Pattern END ________________________________________________

    /**
     * The matrix is represented as triangular matrix
     * number of fields of an triangular matrix (symmetrical) with n elements: (( n (n + 1)) / 2)
     */
    public static int bitMatrixArraySize = MatrixCalcUtil.calcGauss(Constants.NUMBER_BITS_PER_BUS); // = 401.856

    /**
     * array holding all ActionSequenceWrapper of the matrix
     */
    private ActionSequenceWrapper[] bitMatrixArray;


    /**
     *
     * @param fieldIndex
     * @return
     */
    public ActionSequenceWrapper getBitMatrixField(int fieldIndex) {
        return bitMatrixArray[fieldIndex];
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

        // rmx - 1 because rmx sends busRMX1 as 1
        conditionOne[0] -= conditionOne[0];
        conditionTwo[0] -= conditionTwo[0];

        // calculate bitIndex of both conditions
        int bitIndexConditionOne = MatrixCalcUtil.calcBitIndex(conditionOne[0], conditionOne[1], conditionOne[2]);
        int bitIndexConditionTwo = MatrixCalcUtil.calcBitIndex(conditionTwo[0], conditionTwo[1], conditionTwo[2]);

        // calculation of the pointindex = gaussian value of bigger + bitIndex of smaller
        int pointIndex;

        //check to determine which bit index of the two conditions is bigger
        if (bitIndexConditionOne >= bitIndexConditionTwo) {
            // bit index condition one is bigger
            pointIndex = MatrixCalcUtil.calcGauss(bitIndexConditionOne) + bitIndexConditionTwo;
            addActionSequenceToActionSequenceWrapper(conditionOne, conditionTwo, actionSequence, pointIndex);
        } else {
            // bit index condition one is bigger
            pointIndex = MatrixCalcUtil.calcGauss(bitIndexConditionTwo) + bitIndexConditionOne;
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
        if (bitMatrixArray[pointIndex] == null) {
            bitMatrixArray[pointIndex] = new ActionSequenceWrapper();
        }

        // set action sequence in the right Action wrapper aat the pointIndex
        bitMatrixArray[pointIndex].setActionSequence(bitValue_rowIndex, bitValue_columnIndex, actionSequence);
    }
}
