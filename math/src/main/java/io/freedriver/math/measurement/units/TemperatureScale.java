package io.freedriver.math.measurement.units;

import java.util.stream.Stream;

public enum TemperatureScale implements Unit {
    KELVIN("Kelvin", "K"),
    CELSUIS("Celsuis", "C"),
    FAHRENHEIT("Fahrenheit", "F")
    ;

    private final String singular;
    private final String symbol;

    TemperatureScale(String singular, String symbol) {
        this.singular = singular;
        this.symbol = symbol;
    }

    public static Stream<TemperatureScale> stream() {
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
