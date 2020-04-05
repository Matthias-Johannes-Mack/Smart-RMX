package matrix;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import action.ActionSequence;
import bus.Bus;
import bus.BusDepot;

/**
 * Class for the Matrix
 *
 * @author Team SmartRMX
 */
public class Matrix {
	// (( n (n + 1)) / 2)
	// Formel für Dreiecksmatrix = (((112*8 Bit) ((112*8 Bit) + 1 ) ) / 2 )
	final static int arraySize = (((112 * 8) * ((112 * 8) + 1)) / 2);
	static public ActionSequence[] matrix;

	// 112 Systemdressen mit je 8 Bit
	final static int NUMBER_BITS_PER_BUS = 896;

	static BusDepot busDepot;

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
			instance.busDepot = BusDepot.getBusDepot();
		}
		return instance;
	}

	// Singleton-Pattern END ________________________________________________
	/**
	 * Method, that creates the matrix
	 */
	private void createMatrix() {
		// create the matrix with fixed arraySize
		matrix = new ActionSequence[arraySize];
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

		// rmx - 1 because rmx sends busRMX1 as 1
		int bus = rmx - 1;
		int systemadresse = adrRMX;

		// iterates over set bits of lastChanged
		BitSet valueBitSet = BitSet.valueOf(new byte[] { lastChanged });

		for (int i = valueBitSet.nextSetBit(0); i >= 0; i = valueBitSet.nextSetBit(i + 1)) {
			// bit i in value is set => check this bit
			calcPos(bus, systemadresse, i);
		}
	}

	/**
	 * Method, that calculates the position of a specified element
	 * 
	 * @param bus includes bus id of RMX bus -1
	 * @param systemadresse
	 * @param bit
	 */
	public static List<ActionSequence> calcPos(int bus, int systemadresse, int bit) {

		List<ActionSequence> result = new ArrayList<>();

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

		// current bus bits are checked
		Bus currentBus = null;
		//TODO refactor das hier net mit null gemacht wird (wird zwar direkt in if gesprungen, muss aber initialisiert sein)

		// Bernds-Formel to the right
		// loop through entire row to the right
		for (int columnIndex = 0; columnIndex <= bitIndex; columnIndex++) {

			if(columnIndex % NUMBER_BITS_PER_BUS == 0) {

				// get current bus => + 1 since RMX starts counting at 1
				currentBus = busDepot.getBus(((columnIndex / NUMBER_BITS_PER_BUS) + 1));
			}

			// systemadress of current bitIndex that is being checked
			int systemadress_checkedBit = getSystemadressByBitIndex(columnIndex);

			if(currentBus.isBitSet(systemadress_checkedBit, bit)) {
				//both conditions are true => get ActionSequence of point in matrix
				ActionSequence actionSequence = matrix[startPoint];

				if(actionSequence != null){
					// ActionSequence for point exists
					result.add(actionSequence);
				}
			}

			startPoint++; // move to the right in the row
		}


		// ----------------------------
		// For the column
		// ----------------------------
		// Gausche' Summenformel (Startpunkt + 1) + startpunkt; startpunkt++ in einer
		// for-loop bis <= max index

		System.out.println("-------------COLUMN---------------");

		// current bus of rowIndex (initially given bus by method)
		currentBus = busDepot.getBus(bus);

		int oldBitIndex = bitIndex;

		bitIndex++;
		int columnPointIndex = calcGauss(bitIndex) + oldBitIndex;

		while (columnPointIndex < arraySize) {
			System.out.println(columnPointIndex);

			if(bitIndex % NUMBER_BITS_PER_BUS == 0) {

				// get current bus => + 1 since RMX starts counting at 1
				currentBus = busDepot.getBus(((bitIndex / NUMBER_BITS_PER_BUS) + 1));
			}

			// check condition
			// systemadress of current bitIndex that is being checked
			int systemadress_checkedBit = getSystemadressByBitIndex(bitIndex);

			if(currentBus.isBitSet(systemadress_checkedBit, bit)) {
				//both conditions are true => get ActionSequence of point in matrix
				ActionSequence actionSequence = matrix[startPoint];

				if(actionSequence != null){
					// ActionSequence for point exists
					result.add(actionSequence);
				}
			}

			bitIndex++;
			columnPointIndex = calcGauss(bitIndex) + oldBitIndex;
		}

		// return ActionSequences of true conditions
		return result;
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
	 * @param actionSequence
	 */
	public static void addAction(Integer[] conditionOne, Integer[] conditionTwo, ActionSequence actionSequence) {
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
		matrix[pointIndex] = actionSequence;

	}

	public static int getSystemadressByBitIndex(int bitIndex) {
		return ((bitIndex % NUMBER_BITS_PER_BUS) / 8); // cuts decimal places
	}
}
