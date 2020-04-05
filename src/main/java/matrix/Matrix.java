package matrix;

import java.util.BitSet;

import action.Actions;

/**
 * Class for the Matrix
 *
 * @author Team SmartRMX
 */
public class Matrix {
	// (( n (n + 1)) / 2)
	// Formel f�r Dreiecksmatrix = (((112*8 Bit) ((112*8 Bit) + 1 ) ) / 2 )
	final static int arraySize = (((112 * 8) * ((112 * 8) + 1)) / 2);
	static public Actions[] matrix;
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
		matrix = new Actions[arraySize];
	}

	/**
	 * Method if changes happen, called by the schedular Indexs of rmx, adrRMX and
	 * lastChanged start at 0!!!
	 * 
	 * @param rmx
	 * @param adrRMX
	 * @param lastChanged
	 */
	private void check(byte rmx, byte adrRMX, byte lastChanged) {

		BitSet valueBitSet = BitSet.valueOf(new byte[] { lastChanged });
		for (int i = valueBitSet.nextSetBit(0); i >= 0; i = valueBitSet.nextSetBit(i + 1)) {
			// bit i in value is set

		}
	}

	/**
	 * Method, that calculates the position of a specified element
	 * 
	 * @param bus
	 * @param systemadresse
	 * @param bit
	 */
	public static void calcPos(int bus, int systemadresse, int bit) {

		// ----------------------------
		// For the row
		// ----------------------------
		System.out.println("-------------ROW---------------");


		// formula of Bernd Schneider
		// bitIndex = bus + systemadresse * 8 (bit) + gesetzter Bit (i)
		// bitIndex -> die Zeile
		int bitIndex = calcBerndFormula(bus, systemadresse, bit);

		// Gaussche' Summenformel
		// index of first field in row
		int startPoint = calcGauss(bitIndex);
		// Bernds-Formel to the right
		// loop through entire row to the right
		for (int i = 0; i <= bitIndex; i++) {
			// TODO check all fields of row mit startpoint
			System.out.println(startPoint);
			startPoint++; // move to the right in the row
		}
		// ----------------------------
		// For the column
		// ----------------------------
		// Gausche' Summenformel (Startpunkt + 1) + startpunkt; startpunkt++ in einer
		// for-loop bis <= max index

		System.out.println("-------------COLUMN---------------");

		int oldBitIndex = bitIndex;

		bitIndex++;
		int columnPointIndex = calcGauss(bitIndex) + oldBitIndex;

		while (columnPointIndex < arraySize) {
			System.out.println(columnPointIndex);

			bitIndex++;
			columnPointIndex = calcGauss(bitIndex) + oldBitIndex;
			//TODO check field
		}
		
	}

	/**
	 * Method, that calculates Bernds Formula
	 * 
	 * @param bus
	 * @param systemadresse
	 * @param bit
	 * @return
	 */
	public static int calcBerndFormula(int bus, int systemadresse, int bit) {
		return (bus * 112) + (systemadresse * 8) + bit;
	}

	/**
	 * calculates the gaussian sum forumla
	 *
	 * @param n
	 * @return
	 */
	public static int calcGauss(int n) {
		return (((n * n) + n) / 2);
	}

	/**
	 *
	 * the passed Integer Arrays need to be two DIFFERENT references
	 *
	 * format of condition: [bus][systemadresse][bit]
	 * @param conditionOne
	 * @param conditionTwo
	 * @param actions
	 */
	public static void addAction(Integer[] conditionOne, Integer[] conditionTwo, Actions actions) {
		//format [bus][systemadresse][bit]

		// rmx - 1 because rmx sends busRMX1 as 1
		conditionOne[0] -= conditionOne[0];
		conditionTwo[0] -= conditionTwo[0];

		// calculate bernd value of both conditions
		int berndOne = calcBerndFormula(conditionOne[0], conditionOne[1], conditionOne[2]);
		int berndTwo = calcBerndFormula(conditionTwo[0], conditionTwo[1], conditionTwo[2]);


		// calculation of the pointindex = gaussian value of bigger + bernd of smaller
		int pointIndex;

		if(berndOne >= berndTwo) {
			pointIndex = calcGauss(berndOne) + berndTwo;

		} else {
			// berndTwo is bigger
			pointIndex = calcGauss(berndTwo) + berndOne;
		}

		// add actions to calculated index
		matrix[pointIndex] = actions;

	}
}
