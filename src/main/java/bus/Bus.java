package bus;

import Utilities.ByteUtil;

import java.util.ArrayList;
import java.util.BitSet;

public class Bus {

	/**
	 * amount of systemadressen in each bus RMX-PC-Zentrale: 112 adressen (index 0
	 * to 111)
	 */
	public static final int NUMBER_SYSTEMADRESSEN = 112;

	public volatile int busId;

	public volatile byte[] systemadressen;

	/**
	 * ArraList contains Integer Arrays of length 8 to represent a byte starting from index 0 - 7
	 * -1 = no changes at bit, 0 = bit changed to 0, 1 bit changed to 1
	 */
	public volatile ArrayList<Integer[]> lastChanges;

	public Bus(byte rmx) {
		busId = rmx;
		systemadressen = new byte[NUMBER_SYSTEMADRESSEN]; // initial all values are 0
		lastChanges = new ArrayList<>(NUMBER_SYSTEMADRESSEN);
		initalizeArrayList();
	}

	/**
	 * initalizes the lastChanges list
	 */
	private void initalizeArrayList() {
		for(int i=0; i < NUMBER_SYSTEMADRESSEN; i++) {
			lastChanges.add(new Integer[]{-1, -1, -1, -1, -1, -1, -1, -1});
		}
	}

	/**
	 * updates adress and changes. updates changes to 1 and changes to 0
	 * @param adrrmx
	 * @param value
	 */
	public void updateBusAdress(byte adrrmx, byte value) {

		BitSet currentBitSet = BitSet.valueOf(new byte[] { systemadressen[adrrmx] });
		BitSet valueBitSet = BitSet.valueOf(new byte[] { value });

		Integer[] changes = new Integer[8];


		System.out.println("new"+value);
		System.out.println("current"+systemadressen[adrrmx]);


		// iterates every every bit
		for (int i = 0; i < 8; i++) {
			boolean currentBit = currentBitSet.get(i);
			boolean valueBit = valueBitSet.get(i);

			System.out.println("currentBit "+ currentBit);
			System.out.println("valueBit "+ valueBit);

			// bit i in value is set
			if (valueBit != currentBit) {

				System.out.println("wir sind verschiedn");

				if(valueBit == true) {
					changes[i] = 1;
				} else {
					changes[i] = 0;
				}

				System.out.println("nach änderung "+changes[i]);

			} else {
				//no changes, value stays at -1
				System.out.println("no changes");
				changes[i] = -1;
				System.out.println("nach änderung "+changes[i]);

			}
		}



		lastChanges.set(adrrmx, changes);

		// update current value
		systemadressen[adrrmx] = value;
	}

	/**
	 * Gibt die letzten Änderungen der übergebenen Systemadresse zurück
	 *
	 * @param adrrmx
	 * @return
	 */
	public Integer[] getChanges(byte adrrmx) {
		return lastChanges.get(adrrmx);
	}

	/**
	 * Checks if the bit of the given systemadress is set
	 * 
	 * @param systemadresse
	 * @param bit
	 * @return true if bit is set, returns false if bit isnt set
	 */
	public boolean isBitSet(int systemadresse, int bit) {
		return ByteUtil.bitIsSet(systemadressen[systemadresse], bit);
	}

	/**
	 * Method that returns the current Byte
	 * 
	 * @param systemAdresse
	 * @return
	 */
	public byte getCurrentByte(int systemAdresse) {
		return systemadressen[systemAdresse];
	}

}