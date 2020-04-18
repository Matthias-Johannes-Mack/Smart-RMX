package matrix.byteMatrix;

import matrix.factory.ByteRule;
import utilities.Constants;
import matrix.matrixutilities.MatrixCalcUtil;

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
            instance.byteMatrixArray = new ByteRuleWrapper[arraySize];
        }

        return instance;
    }

    // Singleton-Pattern END ________________________________________________

    /**
     * The ByteMatrix is represented as triangular matrix
     * number of fields of an triangular matrix (symmetrical) with n elements: (( n (n + 1)) / 2)
     */
    private static int arraySize = MatrixCalcUtil.calcGauss(Constants.NUMBER_SYSTEMADRESSES_PER_BUS); // = 6.328

    /**
     * array holding all ActionSequenceWrapper of the matrix
     */
    private ByteRuleWrapper[] byteMatrixArray;



    public ByteRuleWrapper getByteMatrixField(int fieldIndex) {
        return byteMatrixArray[fieldIndex];
    }


    public void addByteRule(ByteRule rule) {
        //[Bus][Systemadress] smaller index of the two conditions
        Integer[] conditionOneAdress = rule.getByteConditionOne().getConditionAdress();
        //[Bus][Systemadress] bigger index of the two conditions
        Integer[] conditionTwoAdress = rule.getByteConditionTwo().getConditionAdress();
        //[Bus][Systemadress] bigger index of the two conditions


        // rmx - 1 because rmx sends busRMX1 as 1
        conditionOneAdress[0] -= conditionOneAdress[0];
        conditionTwoAdress[0] -= conditionTwoAdress[0];

        // calculate bitIndex of both conditions
        int byteIndexConditionOne = MatrixCalcUtil.calcByteIndex( conditionOneAdress[0],  conditionOneAdress[1]);
        int byteIndexConditionTwo = MatrixCalcUtil.calcByteIndex( conditionTwoAdress[0],  conditionTwoAdress[1]);

        // calculation of the pointindex = gaussian value of bigger + bitIndex of smaller
        int pointIndex;

        //check to determine which bit index of the two conditions is bigger
        //check normally not needed since due to implementation of byte rule condition one is always smaller
        if (byteIndexConditionOne >= byteIndexConditionTwo) {
            // bit index condition one is bigger
            pointIndex = MatrixCalcUtil.calcGauss(byteIndexConditionOne) + byteIndexConditionTwo;
            addByteRuleToMatrix(rule , pointIndex);
        } else {
            // bit index condition one is bigger
            pointIndex = MatrixCalcUtil.calcGauss(byteIndexConditionTwo) + byteIndexConditionOne;
            addByteRuleToMatrix(rule , pointIndex);

        }
    }

    private void addByteRuleToMatrix(ByteRule rule ,int pointIndex){
        // if no action seq wrapper exists at point add new
        if (byteMatrixArray[pointIndex] == null) {
            System.out.println("ICH FÜGE EINEN WRAPPER HINZU ZUR BYTE MATRIX " + pointIndex);
            byteMatrixArray[pointIndex] = new ByteRuleWrapper();
        }

        // set action sequence in the right Action wrapper aat the pointIndex
        byteMatrixArray[pointIndex].addByteRule(rule);
    }
}
