package bus;

import java.util.HashMap;

/**
 * Class that contains all saved Buses
 *
 * All methods need to be synchronized since multiple threads can access the BusDepot at the same time:
 * Receiver-Thread, Schedular-Thread, ActionWait-Thread(s)
 *
 */
public class BusDepot {

    // Singleton-Pattern START -----------------------------------------

    /**
     * Singleton instance of BusDepot
     */
    private static BusDepot instance = null;

    /**
     * private constructor to prevent instantiation
     */
    private BusDepot() {

    }

    /**
     * Returns singleton BusDepot instance
     *
     * @return BusDepot Singleton instance
     */
    public static synchronized BusDepot getBusDepot() {
        if (instance == null) {
            instance = new BusDepot();
        }
        return instance;
    }

    // Singleton-Pattern END ________________________________________________

    /**
     * Map including all Buses
     * Key is given by the send <RMX> Value of an RMXmessage:
     *
     * RMX-1 = Key: 1
     */
    private volatile HashMap<Integer, Bus> busDepotMap = new HashMap<>();


    /**
     * updates given systemadress with the given byteValue of the bus specified by the given busId.
     * If the bus specified by the given busId doesnt exist, the bus is created and then updated.
     *
     * Message: format <0x06><RMX><ADRRMX><VALUE>
     *
     * @param busId
     * @param systemadress
     * @param byteValue
     */
    public synchronized void updateBus(int busId, int systemadress, int byteValue) {

        if(!busExists(busId)){
            // the bus doesnt exist => create Bus with given busid
            Bus newBus = new Bus(busId);

            // update bus with given message
            newBus.updateBusAdress(systemadress, byteValue);
            System.out.println("New Bus created: " + busId + " and updated Systemadresse: " + systemadress + " ByteValue: " + byteValue);

            // add the bus to the depot
            busDepotMap.put(busId, newBus);
        } else {

            // bus already exists => only need to update
            Bus bus = getBus(busId); // get Bus by Id
            bus.updateBusAdress(systemadress, byteValue);

            System.out.println("Updated Bus: " + busId + " Systemadresse: " + systemadress + " ByteValue: " + byteValue);
        }
    }

    /**
     * Returns the changes of the given systemadress of the given bus specified by the busId
     * The Integer Array (length 8) represent a byte with bits starting from index 0 - 7.
     * The value of each bit can be: -1 = no changes at bit, 0 = bit changed to 0, 1 bit changed to 1
     *
     * @param busId
     * @param systemadress
     * @return Integer[] size 8 that represents the last Changes of the given systemadress
     */
    public synchronized Integer[] getChanges(int busId, int systemadress) {

        // get changes of given systemadress
        Bus bus = getBus(busId);
        Integer[] changes = bus.getChanges(systemadress);

        return changes;
    }

    /**
     * Returns the changes of the given systemadress of the given bus specified by the busId and updates
     * the given systemadress with the given byteValue of the bus specified by the given busId.
     * The Integer Array (length 8) represent a byte with bits starting from index 0 - 7.
     * The value of each bit can be: -1 = no changes at bit, 0 = bit changed to 0, 1 bit changed to 1
     *
     * @param busId
     * @param systemadress
     * @param byteValue
     * @return Integer[] size 8 that represents the last Changes of the given systemadress
     */
    public synchronized Integer[] getChangesAndUpdate(int busId, int systemadress, int byteValue) {

        // update bus
        updateBus(busId, systemadress, byteValue);

        // get changes of given systemadress
        Bus bus = getBus(busId);
        Integer[] changes = bus.getChanges(systemadress);

        return changes;
    }


    /**
     * Returns the bus specified by the given busId
     * @param busId
     * @return the bus specified by the given busId, null if no bus with the given busId exists
     */
    public synchronized Bus getBus(int busId) {
        return busDepotMap.get(busId);
    }

    /**
     * Checks if the bus specified by the given busId exists in the BusDepot
     * @param busId
     * @return true if the bus specified by the given busId exits, false otherwise
     */
    public synchronized boolean busExists(int busId) {
        return busDepotMap.containsKey(busId);
    }

}