package bus;

import java.util.HashMap;

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
     * Key 1 - RMX-1 (Key ergibt sich aus der übergebenen "RMX" Wert der OPCODE Ox06 Nachricht)
     * ... ggf. wenn wir erweitern müssen weitere
     */
    private HashMap<Integer, Bus> busDepotMap = new HashMap<>();


    /**
     * updates Bus - if the bus isnt saved yet, the method saves the bus and then updates the given adress
     * format <0x06><RMX><ADRRMX><VALUE>
     *
     * @param message
     */
    public synchronized void updateBus(byte[] message) {
        byte rmx = message[1]; // busId
        byte adrrmx = message[2]; // Systemadresse
        byte value = message[3]; // gesetzte Bits

        if(!busExists(rmx)){
            // doesnt exist --> create Bus with given values
            Bus newBus = new Bus(rmx);
            newBus.updateBusAdress(adrrmx, value);
            System.out.println("New Bus created: " + rmx + " and updated Systemadresse: " + adrrmx + " Value: " + value);
        } else {
            // only need to update
            Bus bus = getBus(rmx); // get Bus by Id
            bus.updateBusAdress(adrrmx, value);

            System.out.println("Updated Bus: " + rmx + " Systemadresse: " + adrrmx + " Value: " + value);
        }
    }

    /**
     * updates ADRRMX of Bus RMX with VALUE then returns only the changes as a byte.
     *
     * example:
     * - current: 10000000
     * - message: 10000001
     * - return:  00000001
     *
     * format <0x06><RMX><ADRRMX><VALUE>
     *
     * @param message
     */
    public synchronized byte getChanges(byte[] message) {
        byte rmx = message[1]; // busId
        byte adrrmx = message[2]; // Systemadresse

        // update bus
        updateBus(message);

        // get changes of given ADRRMX
        Bus bus = getBus(rmx);
        byte changes = bus.getChanges(adrrmx);

        return changes;
    }


    public synchronized Bus getBus(int busId) {
        return busDepotMap.get(busId);
    }

    private synchronized boolean busExists(int busId) {
        return busDepotMap.containsKey(busId);
    }

    public synchronized void removeBus(int busId) {
        busDepotMap.remove(busId);
    }

    public synchronized void clearTrain() {
        busDepotMap.clear();
    }
}