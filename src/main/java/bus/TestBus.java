package bus;

import java.util.Arrays;

public class TestBus {
    public static void main(String[] args) {

        byte id = 1;

        Bus bus = new Bus(id);

        //System.out.println("Last changed: " + bus.lastChanges.get(0));
        //System.out.println("Value: " + Arrays.toString()bus.systemadressen[0]);
        //System.out.println("------------------");

        byte adress = 0;
        byte value = 9;


        bus.updateBusAdress(adress,value);

        System.out.println("Last changed: " + Arrays.toString(bus.lastChanges.get(0)));
        System.out.println("Value: " + bus.systemadressen[0]);
        System.out.println("------------------");

        value = 0;

        bus.updateBusAdress(adress,value);
        System.out.println("Last changed: " + Arrays.toString(bus.lastChanges.get(0)));
        System.out.println("Value: " + bus.systemadressen[0]);
        System.out.println("------------------");

    }
}
