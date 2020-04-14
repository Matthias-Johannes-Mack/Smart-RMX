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
	private volatile int[] systemadressen;

	/**
	 * ArrayList that contains the last Changes of each systemadresse (byte) in comparison to the last state
	 * - the containing integer Arrays (length 8) represent a byte with bits starting from index 0 - 7
	 * - the value of each bit can be: -1 = no changes at bit, 0 = bit changed to 0, 1 bit changed to 1
	 */
	private volatile ArrayList<Integer[]> lastChanges;

	/**
	 * Constructor for a Bus
	 * @param busId busid of the Bus to create
	 */
	public Bus(int busId) {
		this.busId = busId;
		systemadressen = new int[Constants.NUMBER_SYSTEMADRESSES_PER_BUS]; // initial all values are 0
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
	 * @param systemadress systemadress to update
	 * @param byteValue to update the systemadress to
	 *
	 */
	public void updateBusAdress(int systemadress, int byteValue) {


		BitSet currentBitSet = BitSet.valueOf(new long[]{systemadressen[systemadress]});
		BitSet valueBitSet = BitSet.valueOf(new long[]{ byteValue });

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
		lastChanges.set(systemadress, changes);

		// update the currently safed value
		systemadressen[systemadress] = byteValue;
	}

	/**
	 * Returns the last changes of the given systemadressse
	 *
	 * @param systemadress systemadress to get the changes for
	 * @return Integer[] size 8 that represents the last Changes of the given systemadress
	 */
	public Integer[] getChanges(int systemadress) {
		return lastChanges.get(systemadress);
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
	 * @param systemadresse the systemadress to get current value for
	 * @return byte value of the given systemadress
	 */
	public int getCurrentByte(int systemadresse) {
		return systemadressen[systemadresse];
	}

	/**
	 * @return the id of the bus
	 */
	public int getBusId() {
		return busId;
	}
}