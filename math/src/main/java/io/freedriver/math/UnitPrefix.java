package io.freedriver.math;

import io.freedriver.math.measurement.types.electrical.Current;
import io.freedriver.math.measurement.types.electrical.Energy;
import io.freedriver.math.measurement.types.electrical.Potential;
import io.freedriver.math.measurement.types.electrical.Power;
import io.freedriver.math.number.AutoScaling;
import io.freedriver.math.number.ScaledNumber;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

/**
 * Class to handle metric-style unit scaling.
 */
public enum UnitPrefix implements AutoScaling<UnitPrefix> {
    YOTTA("Yotta", 24, "Y"),
    ZETTA("Zetta", 21, "Z"),
    EXA("Exa", 18, "E"),
    PETA("Peta", 15, "P"),
    TERA("Tera", 12, "T"),
    GIGA("Giga", 9, "G"),
    MEGA("Mega", 6, "M"),
    KILO("Kilo", 3, "k"),
    HECTO("Hecto", 2, "h"),
    DECA("Deca", 1, "da"),
    ONE("",     0, ""),
    DECI("Deci", -1, "d"),
    CENTI("Centi", -2, "c"),
    MILLI("Milli", -3, "m"),
    MICRO("Micro", -6, "μ"),
    NANO("Nano", -9, "n"),
    PICO("Pico", -12, "p"),
    FEMTO("Femto", -15, "f"),
    ATTO("Atto", -18, "a"),
    ZEPTO("Zepto", -21, "z"),
    YOCTO("Yocto", -24, "y");

    private static final List<UnitPrefix> UNCOMMON = Arrays.asList(MICRO, CENTI, DECI, DECA, HECTO);

    private final String name;
    private final int powerOfTen;
    private final String symbol;

    UnitPrefix(String name, int powerOfTen, String symbol) {
        this.name = name;
        this.powerOfTen = powerOfTen;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public int getPowerOfTen() {
        return powerOfTen;
    }

    public BigDecimal getValue() {
        return tenToThePowerOf(powerOfTen);
    }

    public String getSymbol() {
        return symbol;
    }

    public int scale() {
        return getValue().scale();
    }

    public int precision() {
        return getValue().precision();
    }


    public static UnitPrefix nearestUnit(BigDecimal bigDecimal) {
        return UnitPrefix.stream()
                .filter(UnitPrefix::commonOnly)
                .min(UnitPrefix.nearestUnitComparator(bigDecimal))
                .orElseThrow();
    }

    public static boolean commonOnly(UnitPrefix unitPrefix) {
        return UNCOMMON
                .stream()
                .noneMatch(uncommon -> Objects.equals(uncommon, unitPrefix));
    }

    public static boolean uncommonOnly(UnitPrefix unitPrefix) {
        return UNCOMMON
                .stream()
                .anyMatch(uncommon -> Objects.equals(uncommon, unitPrefix));
    }

    public static Comparator<UnitPrefix> nearestUnitComparator(final BigDecimal value) {
        return nearestUnitComparator(value.precision(), value.scale());
    }

    public static Comparator<UnitPrefix> nearestUnitComparator(final int precision, final int scale) {
        if (precision - scale >= 1) { // Number above 1

        }



        return Comparator
                .comparingInt((UnitPrefix u) -> Math.abs(u.precision() - precision))
                .thenComparingInt(u -> Math.abs(u.scale() - scale));
    }

    @Override
    public Stream<UnitPrefix> all() {
        return Stream.of(values());
    }

    @Override
    public UnitPrefix me() {
        return this;
    }

    @Override
    public boolean hasUp() {
        return all()
                .anyMatch(other -> other.isHigherScaleThan(this));
    }

    @Override
    public boolean hasDown() {
        return all()
                .anyMatch(other -> other.isLowerScaleThan(this));
    }

    @Override
    public int scaleCompareTo(UnitPrefix unitPrefix) {
        return Comparator.comparing(UnitPrefix::getPowerOfTen)
                .compare(this, unitPrefix);
    }

    public static Stream<UnitPrefix> stream() {
        return Stream.of(values());
    }

    public static Optional<UnitPrefix> ofPrefix(String prefix) {
        return stream()
                .filter(m -> Objects.equals(m.symbol, prefix))
                .findFirst();
    }

    public static UnitPrefix largest() {
        return Stream.of(values())
                .max(Comparator.comparing(UnitPrefix::getValue))
                .orElseThrow(() -> new RuntimeException("No max!"));
    }

    public static UnitPrefix smallest() {
        return Stream.of(values())
                .min(Comparator.comparing(UnitPrefix::getValue))
                .orElseThrow(() -> new RuntimeException("No min!"));
    }

    public static BigDecimal tenToThePowerOf(int power) {
        if (power>0) {
            return BigDecimal.ONE.movePointRight(power);
        } else if (power<0) {
            return BigDecimal.ONE.movePointLeft(power*-1);
        }
        return BigDecimal.ONE;
    }


    public Current amps(Number number) {
        return new Current(new ScaledNumber(number, this));
    }

    public Power watts(Number number) {
        return new Power(new ScaledNumber(number, this));
    }

    public Potential volts(Number number) {
        return new Potential(new ScaledNumber(number, this));
    }

    public Energy wattHours(Number number) {
        return watts(number).hours();
    }

}
