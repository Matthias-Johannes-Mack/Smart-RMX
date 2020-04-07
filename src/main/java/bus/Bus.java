package bus;

import Utilities.ByteUtil;

import java.util.BitSet;

public class Bus {

	/**
	 * amount of systemadressen in each bus RMX-PC-Zentrale: 112 adressen (index 0
	 * to 111)
	 */
	public static final int NUMBER_SYSTEMADRESSEN = 112;

	public volatile int busId;

	public volatile byte[] systemadressen;

	public volatile byte[] lastChanges;

	public Bus(byte rmx) {
		busId = rmx;
		systemadressen = new byte[NUMBER_SYSTEMADRESSEN]; // initial all values are 0
		lastChanges = new byte[NUMBER_SYSTEMADRESSEN]; // initial all values are 0
	}

	/**
	 *
	 * @param adrrmx
	 * @param value
	 */
	public void updateBusAdress(byte adrrmx, byte value) {

		BitSet currentBitSet = BitSet.valueOf(new byte[] { systemadressen[adrrmx] });
		BitSet valueBitSet = BitSet.valueOf(new byte[] { value });

		BitSet changesBitSet = new BitSet(); // => 00000000

		// only iterates over set bits
		boolean somethingChanged = false;
		for (int i = valueBitSet.nextSetBit(0); i >= 0; i = valueBitSet.nextSetBit(i + 1)) {
			// bit i in value is set
			if (currentBitSet.get(i) == false) {
				// a change has happend
				changesBitSet.set(i);
				somethingChanged = true;
			}
		}

		// update lastChanges
		if (somethingChanged) {
			byte[] changes = changesBitSet.toByteArray();
			lastChanges[adrrmx] = changes[0];
		} else {
			lastChanges[adrrmx] = 0; // nothing changed => lastChanges 00000000
		}

		// update current value
		systemadressen[adrrmx] = value;
	}

	/**
	 * Gibt die letzten Änderungen der übergebenen Systemadresse zurück
	 *
	 * @param adrrmx
	 * @return
	 */
	public byte getChanges(byte adrrmx) {
		return lastChanges[adrrmx];
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