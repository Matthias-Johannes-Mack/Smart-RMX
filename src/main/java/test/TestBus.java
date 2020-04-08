package test;

import bus.Bus;

import java.util.Arrays;

public class TestBus {
    public static void main(String[] args) {

        // creates the bus with the given id all systemadresses are 0 and lastchanges -1
        byte id = 1;
        Bus bus = new Bus(id);


        byte systemadress = 0;


        // change value of systemadress 0 to 9 (00001001)
        byte value1 = 9;
        bus.updateBusAdress(systemadress,value1);
        System.out.println("Last changed: " + Arrays.toString(bus.lastChanges.get(0)));
        System.out.println("Value: " + bus.systemadressen[0]);
        System.out.println("------------------");

        // change value of systemadresss 0 to 11 (00001011)
        byte value2 = 11;
        bus.updateBusAdress(systemadress,value2);
        System.out.println("Last changed: " + Arrays.toString(bus.lastChanges.get(0)));
        System.out.println("Value: " + bus.systemadressen[0]);
        System.out.println("------------------");

        // change value of systemadresss 0 to 9 (00001001)
        byte value3 = 9;
        bus.updateBusAdress(systemadress,value3);
        System.out.println("Last changed: " + Arrays.toString(bus.lastChanges.get(0)));
        System.out.println("Value: " + bus.systemadressen[0]);
        System.out.println("------------------");

    }
}
