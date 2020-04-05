package bus;

public class TestBus {
    public static void main(String[] args) {

        byte id = 1;

        Bus bus = new Bus(id);

        System.out.println("Last changed: " + bus.lastChanges[0]);
        System.out.println("Value: " + bus.systemadressen[0]);
        System.out.println("------------------");

        byte adress = 0;
        byte value = 9;

        bus.updateBusAdress(adress,value);

        System.out.println("Last changed: " + bus.lastChanges[0]);
        System.out.println("Value: " + bus.systemadressen[0]);
        System.out.println("------------------");

        value = 11;

        bus.updateBusAdress(adress,value);
        System.out.println("Last changed: " + bus.lastChanges[0]);
        System.out.println("Value: " + bus.systemadressen[0]);
        System.out.println("------------------");

    }
}
