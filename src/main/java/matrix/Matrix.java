package matrix;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class for the Matrix
 *
 * @author Team SmartRMX
 */
public class Matrix {
	// Arraysize of (112*8Bit)²
	final int arraySize = 802816;
	public Object[] matrix;
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
	 * @return BusDepot Singleton instance
	 */
	public static synchronized Matrix getMatrix() {
		if (instance == null) {
			instance = new Matrix();
			instance.createMatrix();
		}
		return instance;
	}

	// Singleton-Pattern END ________________________________________________
	/**
	 * Method, that creates the matrix
	 */
	private void createMatrix() {
		// create the matrix with fixed arraySize
		matrix = new Object[arraySize];
		matrix[0] = "test";
		matrix[8] = "9";
	}
}
