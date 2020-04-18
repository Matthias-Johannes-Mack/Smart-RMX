package tests_utils;

import static org.junit.Assert.*;

import matrix.matrixutilities.MatrixUtil;
import org.junit.Test;

import utilities.ByteUtil;

/**
 * Class for util tests
 *
 */
public class TestUtils {
	/**
	 * Method that tests the function of the utils
	 */
	@Test
	public void testUtils() {
		byte TenAsByte = (byte) 10;
		byte threeAsByte = (byte) 3;
		byte oneHundredAndNintyFive = (byte) 195;

		// --------------Testing bitIsSet()----------------------
		// true cases
		assertTrue(ByteUtil.bitIsSet(TenAsByte, 1));
		assertTrue(ByteUtil.bitIsSet(TenAsByte, 3));
		// false cases
		assertFalse(ByteUtil.bitIsSet(TenAsByte, 0));
		assertFalse(ByteUtil.bitIsSet(TenAsByte, 2));
		assertFalse(ByteUtil.bitIsSet(TenAsByte, 4));
		assertFalse(ByteUtil.bitIsSet(TenAsByte, 5));
		assertFalse(ByteUtil.bitIsSet(TenAsByte, 6));
		assertFalse(ByteUtil.bitIsSet(TenAsByte, 7));

		// --------------Testing setBitAtPos()--------------
		assertEquals(11, ByteUtil.setBitAtPos(TenAsByte, 0, 1));
		assertEquals(8, ByteUtil.setBitAtPos(TenAsByte, 1, 0));

		// --------------Testing convertToInt()--------------
		assertEquals(771, ByteUtil.convertToInt(threeAsByte, threeAsByte));
		assertEquals(963, ByteUtil.convertToInt(oneHundredAndNintyFive, threeAsByte));

		// --------------Testing convertToBytes()--------------
		byte[] solution = ByteUtil.convertToBytes(963);
		assertEquals(3,solution[0]);
		assertEquals(-61,solution[1]);

		// --------------Testing toggleBitAtPos()--------------
		assertEquals(0, ByteUtil.toggleBitAtPos(1,0));
		assertEquals(8, ByteUtil.toggleBitAtPos(10,1));
		assertEquals(48, ByteUtil.toggleBitAtPos(176,7));
		assertEquals(127, ByteUtil.toggleBitAtPos(255,7));

		// --------------Testing toggleBitAtPos()--------------
		Integer[] byte1 = new Integer[]{1,1,1,1,1,1,1,1};
		assertEquals(255, ByteUtil.getByteByByteArray(byte1));

		Integer[] byte2 = new Integer[]{0,1,0,1,0,1,1,1};
		assertEquals(234, ByteUtil.getByteByByteArray(byte2));
	}

}
