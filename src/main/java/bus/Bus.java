package bus;

public class Bus {

    //TODO für den Schedular muss ich mir für jede Systemadresse merken was sich zuletzt geänder hat, sodass der schedular nur diese
    // bits in der Tabelle prüfen muss. Evtl. für zwei Arrays mit je 111 Plätzen, einmal der allgemeine Zustand der 111 Systemadressen
    // am einfachsten direkt den byte.
    // -- dann noch für jede Systemadresse im 2. Array die zuletzt geänderten bits als byte (die muss schedular dann quasi nur
    // durchgehen).
    // Wenn sich werte ggü des letzte mal nicht verändert (also nur von 1 auf 0 geändert werden - wir beachten nur änderungen von Bus
    // von 0 auf 1) byte am besten komplett 0, d.h. schedular wird dann auch nicht über Tabelle laufen und was checken.
    // wichtig: bei initialisierung kann es sein das bits schon gesetzt sind, also müsste am anfang gegen 00000000 verglichen werden oder?
    // evtl. mit BitSet XOR? aber muss drauf achten das nur änderungen von 0 auf 1 beachtet werden

    public Bus (byte RMX) {

    }

    /**
     *
     * @param adrrmx
     * @param value
     */
    public void updateBusAdress(byte adrrmx, byte value) {
        //TODO
    }

    /**
     * Gibt die letzten Änderungen der übergebenen Systemadresse zurück
     *
     * @param adrrmx
     * @return
     */
    public byte getChanges(byte adrrmx) {
        // TODO
        return 1;
    }
}
