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


    public ActionSequence checkField(Integer[] byteValueSmall, Integer[] byteValueBig, int fieldByteMatrix) {

        ActionSequence result = null;

        if(matrix[fieldByteMatrix] != null) {
            result = matrix[fieldByteMatrix].getActionSequenceByState(byteValueSmall, byteValueBig);
        }

        return result;
    }
}
