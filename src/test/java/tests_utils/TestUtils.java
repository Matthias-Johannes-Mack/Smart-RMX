package tests_utils;

import static org.junit.Assert.*;

import org.junit.Test;

import Utilities.ByteUtil;

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
		for (byte byteSolution : solution) {
			System.out.println(byteSolution);// 3 & -61 = 195 (-61 in Decimal from signed 2's complement form)
		}
	}

}
