package bus;

import Utilities.ByteUtil;
import Utilities.Constants;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * Class that represents a Bus of the RMX-PC-Zentrale
 *
 */
public class Bus {

	/**
	 * id of the bus
	 */
	private volatile int busId;

	/**
	 * array that contains the byte value of each systemadress
	 */
	private volatile byte[] systemadressen;

	/**
	 * ArrayList that contains the last Changes of each systemadresse (byte) in comparison to the last state
	 * - the containing integer Arrays (length 8) represent a byte with bits starting from index 0 - 7
	 * - the value of each bit can be: -1 = no changes at bit, 0 = bit changed to 0, 1 bit changed to 1
	 */
	private volatile ArrayList<Integer[]> lastChanges;

	/**
	 * Constructor for a Bus
	 * @param rmx busid of the Bus to create
	 */
	public Bus(byte rmx) {
		busId = rmx;
		systemadressen = new byte[Constants.NUMBER_SYSTEMADRESSES_PER_BUS]; // initial all values are 0
		lastChanges = new ArrayList<>(Constants.NUMBER_SYSTEMADRESSES_PER_BUS);
		initalizeArrayList(); // sets all values of lastChanges initaial at -1 (no changes)
	}

	/**
	 * initalizes the all containing Integer Arrays in lastChanged to -1 (no changes)
	 */
	private void initalizeArrayList() {
		for(int i = 0; i < Constants.NUMBER_SYSTEMADRESSES_PER_BUS; i++) {
			lastChanges.add(new Integer[]{-1, -1, -1, -1, -1, -1, -1, -1});
		}
	}

	/**
	 * updates the given systemadresss with the given value.
	 * updates lastChanges of the given systemadress by comparing the current and given value
	 *
	 * @param adrrmx systemadress to update
	 * @param value to update the systemadress to
	 *
	 */
	public void updateBusAdress(byte adrrmx, byte value) {

		BitSet currentBitSet = BitSet.valueOf(new byte[] { systemadressen[adrrmx] });
		BitSet valueBitSet = BitSet.valueOf(new byte[] { value });

		Integer[] changes = new Integer[8];

		// iterates every every bit
		for (int i = 0; i < 8; i++) {
			boolean currentBit = currentBitSet.get(i);
			boolean valueBit = valueBitSet.get(i);

			// bit i in value is set
			if (valueBit != currentBit) {

				if(valueBit == true) {
					changes[i] = 1;
				} else {
					changes[i] = 0;
				}
			} else {
				//no changes, value stays at -1
				changes[i] = -1;
			}
		}

		// set lastChanges
		lastChanges.set(adrrmx, changes);

		// update the currently safed value
		systemadressen[adrrmx] = value;
	}

	/**
	 * Returns the last changes of the given systemadressse
	 *
	 * @param adrrmx systemadress to get the changes for
	 * @return Integer[] size 8 that represents the last Changes of the given systemadress
	 */
	public Integer[] getChanges(byte adrrmx) {
		return lastChanges.get(adrrmx);
	}

	/**
	 * Checks if a bit of the given systemadress is set to 1
	 * 
	 * @param systemadresse the systemadress to check
	 * @param bitIndex index of the bit to check (from 0 to 7)
	 * @return true if the bit specified by the bitindex is set, false otherwise
	 */
	public boolean isBitSet(int systemadresse, int bitIndex) {
		return ByteUtil.bitIsSet(systemadressen[systemadresse], bitIndex);
	}

	/**
	 * returns the current byte value of the given systemadress
	 * 
	 * @param systemAdresse the systemadress to get current value for
	 * @return byte value of the given systemadress
	 */
	public byte getCurrentByte(int systemAdresse) {
		return systemadressen[systemAdresse];
	}

	/**
	 * @return the id of the bus
	 */
	public int getBusId() {
		return busId;
	}
}