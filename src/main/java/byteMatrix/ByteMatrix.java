package byteMatrix;

import Utilities.Constants;
import action.ActionSequence;
import action.ActionSequenceWrapper;
import bus.BusDepot;
import matrix.MatrixUtil;

public class ByteMatrix {

    // Singleton-Pattern START -----------------------------------------

    /**
     * Singleton instance of ByteMatrix
     */
    private static ByteMatrix instance = null;

    /**
     * private constructor to prevent instantiation
     */
    private ByteMatrix() {

    }

    /**
     * Returns singleton ByteMatrix instance
     *
     * @return ByteMatrix Singleton instance
     */
    public static synchronized ByteMatrix getMatrix() {
        if (instance == null) {
            instance = new ByteMatrix();
            instance.matrix = new ByteRuleWrapper[arraySize];
        }

        return instance;
    }

    // Singleton-Pattern END ________________________________________________

    /**
     * The ByteMatrix is represented as triangular matrix
     * number of fields of an triangular matrix (symmetrical) with n elements: (( n (n + 1)) / 2)
     */
    private static int arraySize = MatrixUtil.calcGauss(Constants.NUMBER_SYSTEMADRESSES_PER_BUS); // = 6.328

    /**
     * array holding all ActionSequenceWrapper of the matrix
     */
    private ByteRuleWrapper[] matrix;


    public ActionSequence checkField(byte byteValueSmall, byte byteValueBig, int fieldByteMatrix) {

        ActionSequence result = null;

        if(matrix[fieldByteMatrix] != null) {
            result = matrix[fieldByteMatrix].getActionSequenceByState(byteValueSmall, byteValueBig);
        }

        return result;
    }

    public void addByteRule(ByteRule rule) {
        //[Bus][Systemadress] smaller index of the two conditions
        Integer[] conditionOneAdress = rule.getConditionOne().getConditionAdress();
        //[Bus][Systemadress] bigger index of the two conditions
        Integer[] conditionTwoAdress = rule.getConditionTwo().getConditionAdress();
        //[Bus][Systemadress] bigger index of the two conditions


        // rmx - 1 because rmx sends busRMX1 as 1
        conditionOneAdress[0] -= conditionOneAdress[0];
        conditionTwoAdress[0] -= conditionTwoAdress[0];

        // calculate bitIndex of both conditions
        int byteIndexConditionOne = MatrixUtil.calcByteIndex( conditionOneAdress[0],  conditionOneAdress[1]);
        int byteIndexConditionTwo = MatrixUtil.calcByteIndex( conditionTwoAdress[0],  conditionTwoAdress[1]);

        // calculation of the pointindex = gaussian value of bigger + bitIndex of smaller
        int pointIndex;

        //check to determine which bit index of the two conditions is bigger
        //check nomrally not needed since due to implementation of byte rule condition one is always smaller
        if (byteIndexConditionOne >= byteIndexConditionTwo) {
            // bit index condition one is bigger
            pointIndex = MatrixUtil.calcGauss(byteIndexConditionOne) + byteIndexConditionTwo;
            addByteRuleToMatrix(rule , pointIndex);
        } else {
            // bit index condition one is bigger
            pointIndex = MatrixUtil.calcGauss(byteIndexConditionTwo) + byteIndexConditionOne;
            addByteRuleToMatrix(rule , pointIndex);

        }
    }

    private void addByteRuleToMatrix(ByteRule rule ,int pointIndex){
        // if no action seq wrapper exists at point add new
        if (matrix[pointIndex] == null) {
            System.out.println("ICH FÜGE EINEN WRAPPER HINZU ZUR BYTE MATRIX " + pointIndex);
            matrix[pointIndex] = new ByteRuleWrapper();
        }

        // set action sequence in the right Action wrapper aat the pointIndex
        matrix[pointIndex].addByteRule(rule);
    }
}
