package test.bus;

import bus.BusDepot;

import java.util.Arrays;

public class TestBusDepot {


    public static void main(String[] args) {

        // format <0x06><RMX><ADRRMX><VALUE>
        byte[] message1 = new byte[]{0x06, 0x00, 0x00, 0x01};

        BusDepot busDepot = BusDepot.getBusDepot();
        System.out.println(busDepot.getBus(0)); // null => bus isnt added yet
        System.out.println(busDepot.busExists(0)); // false

        // adds bus 0 and updates systemadress 0 to 1
        busDepot.updateBus(message1);
        System.out.println(busDepot.busExists(0)); // true
        System.out.println(busDepot.getBus(0).getBusId()); // 0
        System.out.println(busDepot.getBus(0).getCurrentByte(0)); // 1
        System.out.println(Arrays.toString(busDepot.getBus(0).getChanges((byte) 0))); // [1, -1, -1, -1, -1, -1, -1, -1]

        // test getChanges
        System.out.println(Arrays.toString(busDepot.getChanges(message1))); // [1, -1, -1, -1, -1, -1, -1, -1]

        // format <0x06><RMX><ADRRMX><VALUE>
        byte[] message2 = new byte[]{0x06, 0x00, 0x00, 0x00};
        // test getChanges and Update
        System.out.println(Arrays.toString(busDepot.getChangesAndUpdate(message2))); // [0, -1, -1, -1, -1, -1, -1, -1]
    }
}
