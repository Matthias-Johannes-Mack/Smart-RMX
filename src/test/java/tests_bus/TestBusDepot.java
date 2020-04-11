package tests_bus;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import bus.BusDepot;

/**
 * Class to test the BusDepot
 *
 */
public class TestBusDepot {
	/**
	 * Method to test the bus depot
	 */
	@Test
	public void testBusDepot() {
		// format <0x06><RMX><ADRRMX><VALUE>
		byte[] message1 = new byte[] { 0x06, 0x00, 0x00, 0x01 };
		// get the bus depot
		BusDepot busDepot = BusDepot.getBusDepot();
		// null => bus isnt added yet
		assertNull(busDepot.getBus(0));
		// false
		assertFalse(busDepot.busExists(0));

		// adds bus 0 and updates systemadress 0 to 1
		busDepot.getBusDepot().updateBus((byte) 0, message1[2], message1[3]);
		// true
		assertTrue(busDepot.busExists(0));
		// 0
		assertEquals(0, busDepot.getBus(0).getBusId());
		// 1
		assertEquals(1, busDepot.getBus(0).getCurrentByte(0));
		// [1, -1, -1, -1, -1, -1, -1, -1]
		assertArrayEquals(new Integer[] { 1, -1, -1, -1, -1, -1, -1, -1 }, busDepot.getBus(0).getChanges((byte) 0));

		// test getChanges
		// [1, -1, -1, -1, -1, -1, -1, -1]
		assertArrayEquals(new Integer[] { 1, -1, -1, -1, -1, -1, -1, -1 },
				busDepot.getBusDepot().getChanges((byte) 0, message1[2]));

		// format <0x06><RMX><ADRRMX><VALUE>
		byte[] message2 = new byte[] { 0x06, 0x00, 0x00, 0x00 };
		// test getChanges and Update
		// [0, -1, -1, -1, -1, -1, -1, -1]
		assertArrayEquals(new Integer[] { 0, -1, -1, -1, -1, -1, -1, -1 },
				busDepot.getBusDepot().getChangesAndUpdate((byte) 0, message2[2], message2[3]));
	}

}
