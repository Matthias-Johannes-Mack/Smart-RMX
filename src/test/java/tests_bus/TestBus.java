package tests_bus;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import bus.Bus;

/**
 * Class for bus tests
 *
 */
public class TestBus {

	@Test
	/**
	 * Method that tests the bus
	 */
	public void testBus() {
		// creates the bus with the given id all systemadresses are 0 and lastchanges -1
		byte id = 1;
		Bus bus = new Bus(id);

		byte systemadress = 0;

		// change value of systemadress 0 to 9 (00001001)
		byte value1 = 9;
		bus.updateBusAdress(systemadress, value1);
		// [1, -1, -1, 1, -1, -1, -1, -1]
		System.out.println("Last changed: " + Arrays.toString(bus.getChanges((byte) 0)));
		assertArrayEquals(new Integer[] { 1, -1, -1, 1, -1, -1, -1, -1 }, bus.getChanges((byte) 0));
		System.out.println("Value: " + bus.getCurrentByte((byte) 0)); // 9
		assertEquals(9, bus.getCurrentByte((byte) 0));
		System.out.println("------------------");

		// change value of systemadresss 0 to 11 (00001011)
		byte value2 = 11;
		bus.updateBusAdress(systemadress, value2);
		// [-1, 1, -1, -1, -1, -1, -1, -1]
		System.out.println("Last changed: " + Arrays.toString(bus.getChanges((byte) 0)));
		assertArrayEquals(new Integer[] { -1, 1, -1, -1, -1, -1, -1, -1 }, bus.getChanges((byte) 0));
		System.out.println("Value: " + bus.getCurrentByte((byte) 0)); // 11
		assertEquals(11, bus.getCurrentByte((byte) 0));
		System.out.println("------------------");

		// change value of systemadresss 0 to 9 (00001001)
		byte value3 = 9;
		bus.updateBusAdress(systemadress, value3);
		// [-1, 0, -1, -1, -1, -1, -1, -1]
		System.out.println("Last changed: " + Arrays.toString(bus.getChanges((byte) 0)));
		assertArrayEquals(new Integer[] { -1, 0, -1, -1, -1, -1, -1, -1 }, bus.getChanges((byte) 0));
		System.out.println("Value: " + bus.getCurrentByte((byte) 0));
		assertEquals(9, bus.getCurrentByte((byte) 0));
		System.out.println("------------------");
	}

}
