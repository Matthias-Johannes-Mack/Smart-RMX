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
	// Formel für Dreiecksmatrix = (((112*8 Bit) ((112*8 Bit) + 1 ) ) / 2 )
	final static int arraySize = (((112 * 8) * ((112 * 8) + 1)) / 2);
	public Actions[] matrix;
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
		int bus, systemadresse;
		// rmx - 1 because rmx sends busRMX1 as 1
		bus = rmx - 1;
		systemadresse = adrRMX;
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

		// formula of Bernd Schneider
		// bitIndex = bus + systemadresse * 8 (bit) + gesetzter Bit (i)
		// bitIndex -> die Zeile
		int bitIndex = calcBerndFormula(bus, systemadresse, bit);
		// Gaussche' Summenformel
		// index of first field in row
		int startPoint = calcGauss(bitIndex);
		// Bernds-Formel to the right
		// loop through entire row to the right
		for (int i = 0; i < bitIndex; i++) {
			// TODO check all fields of row
		}
		// ----------------------------
		// For the column
		// ----------------------------
		// Gausche' Summenformel (Startpunkt + 1) + startpunkt; startpunkt++ in einer
		// for-loop bis <= max index
		int columnPointIndex;
		int oldBitIndex = bitIndex;
		do {
			bitIndex++;
			columnPointIndex = calcGauss(bitIndex) + oldBitIndex;
			
			System.out.println(columnPointIndex);
		} while (columnPointIndex < arraySize);
		
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
	 * Method, that calculates Bernds Formula
	 * 
	 * @param bus
	 * @param systemadresse
	 * @param bit
	 * @return
	 */
	public static int calcGauss(int n) {
		return (((n * n) + n) / 2);
	}
}
