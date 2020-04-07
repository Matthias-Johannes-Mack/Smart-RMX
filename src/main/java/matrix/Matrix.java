package matrix;

import java.util.ArrayList;
import java.util.List;

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
    // (( n (n + 1)) / 2)
    // Formel f�r Dreiecksmatrix = (((112*8 Bit) ((112*8 Bit) + 1 ) ) / 2 )
    final static int arraySize = (((112 * 8) * ((112 * 8) + 1)) / 2);
    static public ActionSequenceWrapper[] matrix;

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
        matrix = new ActionSequenceWrapper[arraySize];
    }

    /**
     * Method, that checks all conditions
     *
     * @return
     */
    public List<ActionSequence> checkAllConditions() {
        return null;
    }

    /**
     * Method if changes happen, called by the schedular Indexs of rmx, adrRMX and
     * lastChanged start at 0!!!
     *
     * @param rmx
     * @param adrRMX
     * @param lastChanged
     */
    public List<ActionSequence> check(byte rmx, byte adrRMX, Integer[] lastChanged) {

        List<ActionSequence> result = new ArrayList<>();

        // rmx - 1 because rmx sends busRMX1 as 1
        int bus = rmx - 1;
        int systemadresse = adrRMX;

        // iterates over "bits" of lastChanged
        for (int i = 0; i < 8; i++) {
            // only check the bits that have been changed
            if (lastChanged[i] != -1) {
                // bit i in value is set => check this bit
                result.addAll(traverseMatrixAndCheck(bus, systemadresse, i, lastChanged[i]));
            }

        }

        return result;
    }

    /**
     * traverses the matrix, checks if any conditions are true and returns the commulated actionsequences
     *
     * @param bus           includes bus id of RMX bus -1
     * @param systemadresse index of the systemadrese in which bit that has changed
     * @param bit           index of the bit that has changed
     * @param bitvalue      value to which the bit has changed
     */
    public List<ActionSequence> traverseMatrixAndCheck(int bus, int systemadresse, int bit, int bitvalue) {

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
        // TODO refactor das hier net mit null gemacht wird (wird zwar direkt in if
        // gesprungen, muss aber initialisiert sein)

        // Bernds-Formel to the right
        // loop through entire row to the right
        for (int columnIndex = 0; columnIndex <= bitIndex; columnIndex++) {

            if (columnIndex % NUMBER_BITS_PER_BUS == 0) {
                // get current bus => + 1 since RMX starts counting at 1
                currentBus = busDepot.getBus(((columnIndex / NUMBER_BITS_PER_BUS) + 1));
            }

            // systemadress of current bitIndex that is being checked
            int systemadress_checkedBit = getSystemadressByBitIndex(columnIndex);

            ActionSequence actionSequence;

            // the other bit is currently set
            if (currentBus.isBitSet(systemadress_checkedBit, bit)) {
                // both conditions are true => get ActionSequence of point in matrix


                if (bitvalue == 1) {
                    // bit value of row index =  1, bit value of column index is 1
                    actionSequence = matrix[startPoint].getActionSequence1And1();
                } else {
                    // bit value of row index = 0, bit value of column index is 1
                    actionSequence = matrix[startPoint].getActionSequence0And1();
                }


            } else {
                // the other bit is not set
                if (bitvalue == 1) {
                    // bit value of row index =  1, bit value of column index is 0
                    actionSequence = matrix[startPoint].getActionSequence1And0();
                } else {
                    // bit value of row index =  0, bit value of column index is 0
                    actionSequence = matrix[startPoint].getActionSequence0And0();
                }

            }

            if (actionSequence != null) {
                // ActionSequence for point exists
                result.add(actionSequence);
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
        currentBus = busDepot.getBus(bus + 1); // +1 da in busdepot nach der Busid (nach RMX) gespeichert sind

        int oldBitIndex = bitIndex;

        bitIndex++;
        int columnPointIndex = calcGauss(bitIndex) + oldBitIndex;

        while (columnPointIndex < arraySize) {

            if (bitIndex % NUMBER_BITS_PER_BUS == 0) {
                System.out.println("I BIM DRIN");
                // get current bus => + 1 since RMX starts counting at 1
                currentBus = busDepot.getBus(((bitIndex / NUMBER_BITS_PER_BUS) + 1));
            }

            // check condition
            // systemadress of current bitIndex that is being checked
            int systemadress_checkedBit = getSystemadressByBitIndex(bitIndex);

			ActionSequence actionSequence;

			// the other bit is currently set
			if (currentBus.isBitSet(systemadress_checkedBit, bit)) {
				// both conditions are true => get ActionSequence of point in matrix


				if (bitvalue == 1) {
                    // bit value of row index =  1, bit value of column index is 1
                    actionSequence = matrix[columnPointIndex].getActionSequence1And1();
				} else {
                    // bit value of row index =  0, bit value of column index is 1
                    actionSequence = matrix[columnPointIndex].getActionSequence0And1();
				}

			} else {
				// the other bit is not set
				if (bitvalue == 1) {
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

            bitIndex++;
            columnPointIndex = calcGauss(bitIndex) + oldBitIndex;
        }

        // return ActionSequences of true conditions
        return result;
    }

	public static int getSystemadressByBitIndex(int bitIndex) {
		return ((bitIndex % NUMBER_BITS_PER_BUS) / 8); // cuts decimal places
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
		int bitIndexConditionOne = calcBerndFormula(conditionOne[0], conditionOne[1], conditionOne[2]);
		int bitIndexConditionTwo = calcBerndFormula(conditionTwo[0], conditionTwo[1], conditionTwo[2]);

		// calculation of the pointindex = gaussian value of bigger + bernd of smaller
		int pointIndex;

		//check to determine which bit index of the two conditions is bigger
		if (bitIndexConditionOne >= bitIndexConditionTwo) {
		    // bit index condition one is bigger
			pointIndex = calcGauss(bitIndexConditionOne) + bitIndexConditionTwo;
			addActionSequenceToActionSequenceWrapper(conditionOne, conditionTwo, actionSequence, pointIndex);
		} else {
            // bit index condition one is bigger
			pointIndex = calcGauss(bitIndexConditionTwo) + bitIndexConditionOne;
			addActionSequenceToActionSequenceWrapper(conditionTwo, conditionOne, actionSequence, pointIndex);

		}
    }

//TODO kommentieren

    /**
     * adds the given to the ActionWrapper in the right point (index) of the matrix
     * @param conditionBiggerBitIndex
     * @param conditionSmallerBitIndex
     * @param actionSequence
     * @param pointIndex
     */
    private void addActionSequenceToActionSequenceWrapper(Integer[] conditionBiggerBitIndex, Integer[] conditionSmallerBitIndex, ActionSequence actionSequence, int pointIndex) {
		// Row Index = me = biggerBernd
		// Column Index = other = smallerBernd
		int bitValue_BiggerBitIndex = conditionBiggerBitIndex[3];
		int bitValue_SmallerBitIndex = conditionSmallerBitIndex[3];

		if(bitValue_BiggerBitIndex == 0) {
			if(bitValue_SmallerBitIndex == 0) {
				// me 0, other 0
				addActionSequenceWrapperToMatrix(0,0,actionSequence, pointIndex);
			} else {
				//me 0 other 1
				addActionSequenceWrapperToMatrix(0,1,actionSequence, pointIndex);
			}
		} else {
			if(bitValue_SmallerBitIndex == 0) {
				// me 1, other 0
				addActionSequenceWrapperToMatrix(1,0,actionSequence, pointIndex);
			} else {
				//me 1 other 1
				addActionSequenceWrapperToMatrix(1,1,actionSequence, pointIndex);
			}
		}

	}

	private void addActionSequenceWrapperToMatrix(int me, int other, ActionSequence actionSequence, int pointIndex){
    	if(matrix[pointIndex] == null) {
			matrix[pointIndex] = new ActionSequenceWrapper();
		}

    	// add actions to calculated index
		matrix[pointIndex].setActionSequence(me, other, actionSequence);
	}
}
