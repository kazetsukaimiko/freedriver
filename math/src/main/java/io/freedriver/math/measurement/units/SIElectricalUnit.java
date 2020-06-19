package io.freedriver.math.measurement.units;

import java.util.stream.Stream;

public enum SIElectricalUnit implements Unit {
    WATTS("Watt", "W"),
    AMPS("Ampere", "A"),
    VOLTS("Volt", "V"),
    FARADS("Farad", "F"),
    HENRYS("Henry", "H"),
    JOULES("Joule", "J"),
    COULOMBS("Coulomb", "C"),
    SIEMENS("Siemen", "S"),
    OHMS("Ohm", "Ω");

    private final String singular;
    private final String symbol;

    SIElectricalUnit(String singular, String symbol) {
        this.singular = singular;
        this.symbol = symbol;
    }

    public static Stream<SIElectricalUnit> stream() {
        return Stream.of(values());
    }

    @Override
    public String getSingular() {
        return singular;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }
}
