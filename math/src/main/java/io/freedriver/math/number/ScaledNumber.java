package io.freedriver.math.number;

import io.freedriver.math.UnitPrefix;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiFunction;


public class ScaledNumber extends Number implements NumberOperations<ScaledNumber>, Scaleable<ScaledNumber> {
    public static final ScaledNumber ZERO = new ScaledNumber(BigDecimal.ZERO, UnitPrefix.ONE, 2);
    public static final ScaledNumber ONE = new ScaledNumber(BigDecimal.ONE, UnitPrefix.ONE, 2);
    public static final ScaledNumber TEN = new ScaledNumber(BigDecimal.TEN, UnitPrefix.ONE, 2);
    public static final int DEFAULT_SCALE = 20;

    private final BigDecimal value;
    private final UnitPrefix unitPrefix;
    private final int scale;

    public ScaledNumber(BigDecimal value, UnitPrefix unitPrefix, int scale) {
        this.value = value;
        this.unitPrefix = unitPrefix;
        this.scale = scale;
    }

    public ScaledNumber(Number number, UnitPrefix unitPrefix) {
        this(
                number instanceof BigDecimal
                        ? (BigDecimal) number
                        : BigDecimal.valueOf(number.doubleValue()),
                unitPrefix,
                DEFAULT_SCALE);
    }

    public static ScaledNumber of(Number n) {
        if (n != null) {
            if (n instanceof ScaledNumber) {
                return (ScaledNumber) n;
            }
            return of(BigDecimal.valueOf(n.doubleValue()));
        }
        return null;
    }

    public static ScaledNumber of(BigDecimal value) {
        if (value != null) {
            UnitPrefix nearest = UnitPrefix.nearestUnit(value);
            return new ScaledNumber(ScaledNumber.scaleValueTo(value, UnitPrefix.ONE, nearest), nearest, DEFAULT_SCALE);
        }
        return null;
    }

    public static ScaledNumber of(Number number, UnitPrefix unitPrefix) {
        return new ScaledNumber(
                number instanceof BigDecimal
                        ? (BigDecimal) number
                        : BigDecimal.valueOf(number.doubleValue())
                , unitPrefix,
                DEFAULT_SCALE);
    }

    public BigDecimal getValue() {
        return value;
    }

    public UnitPrefix getUnitPrefix() {
        return unitPrefix;
    }

    public int getScale() {
        return scale;
    }

    @Override
    public int intValue() {
        return descale().intValue();
    }

    @Override
    public long longValue() {
        return descale().longValue();
    }

    @Override
    public float floatValue() {
        return descale().floatValue();
    }

    @Override
    public double doubleValue() {
        return descale().doubleValue();
    }

    public BigDecimal descale() {
        return normalize(scaleValueTo(UnitPrefix.ONE));
    }

    public ScaledNumber round() {
        if (getValue().scale() != scale) {
            return new ScaledNumber(getValue().setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros()
                    , unitPrefix, scale);
        }
        return this;
    }

    @Override
    public ScaledNumber add(ScaledNumber other) {
        return scaledNumberOperation(BigDecimal::add, this, other);
    }


    @Override
    public ScaledNumber subtract(ScaledNumber other) {
        return scaledNumberOperation(BigDecimal::subtract, this, other);
    }


    @Override
    public ScaledNumber multiply(ScaledNumber other) {
        return scaledNumberOperation(BigDecimal::multiply, this, other);
    }


    @Override
    public ScaledNumber divide(ScaledNumber other) {
        return scaledNumberOperation((l, r) -> l.divide(r, scale, RoundingMode.HALF_UP), this, other);
    }

    @Override
    public ScaledNumber abs() {
        return new ScaledNumber(value.abs(), unitPrefix, scale);
    }

    @Override
    public int compareTo(ScaledNumber scaledNumber) {
        return scaleTo(UnitPrefix.ONE)
                .round()
                .getValue()
                .compareTo(scaledNumber.scaleTo(UnitPrefix.ONE)
                        .round()
                        .getValue());
    }

    @Override
    public int scaleCompareTo(ScaledNumber scaledNumber) {
        return compareTo(scaledNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScaledNumber that = (ScaledNumber) o;
        return round().compareTo(that.round()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, unitPrefix);
    }

    private BigDecimal scaleValueTo(UnitPrefix toPrefix) {
        return ScaledNumber.scaleValueTo(getValue(), getUnitPrefix(), toPrefix);
    }

    private static ScaledNumber scaledNumberOperation(BiFunction<BigDecimal, BigDecimal, BigDecimal> biFunction,
                                                     ScaledNumber left, ScaledNumber right) {
        return new ScaledNumber(
                bigDecimalOperation(biFunction, left.scaleValueTo(UnitPrefix.ONE), right.scaleValueTo(UnitPrefix.ONE)),
                UnitPrefix.ONE,
                left.scale)
                .scaleTo(left.getUnitPrefix());
    }

    private static BigDecimal bigDecimalOperation(BiFunction<BigDecimal, BigDecimal, BigDecimal> biFunction,
                                                  BigDecimal left, BigDecimal right) {
        return biFunction.apply(left, right);
    }

    private static BigDecimal scaleValueTo(BigDecimal value, UnitPrefix fromPrefix, UnitPrefix toPrefix) {
        // ONE -> Kilo = (3 - 0 = 3) -> Move left 3 places.
        // ONE -> Milli = (-3 - 0) = -3 -> Move right 3 places.
        if (fromPrefix != toPrefix) {
            int direction = toPrefix.getPowerOfTen() - fromPrefix.getPowerOfTen();
            if (direction > 0) {
                return value
                        .movePointLeft(direction);
            } else if (direction < 0) {
                return value
                        .movePointRight(Math.abs(direction));
            }
        }
        return value;
    }

    public ScaledNumber scaleTo(UnitPrefix otherPrefix) {
        return new ScaledNumber(
                normalize(scaleValueTo(otherPrefix)
                        .setScale(scale, RoundingMode.HALF_UP)
                        .stripTrailingZeros()),
                otherPrefix,
                scale
        );
    }

    private static BigDecimal normalize(BigDecimal input) {
        return new BigDecimal(input
                .stripTrailingZeros()
                .toPlainString());
    }

    @Override
    public ScaledNumber me() {
        return this;
    }

    @Override
    public ScaledNumber up() {
        if (hasUp()) {
            UnitPrefix up = unitPrefix.up();
            return new ScaledNumber(
                    ScaledNumber.scaleValueTo(value, unitPrefix, up),
                    up,
                    scale
            );
        }
        return this;
    }

    @Override
    public ScaledNumber down() {
        if (hasDown()) {
            UnitPrefix down = unitPrefix.down();
            return new ScaledNumber(
                    ScaledNumber.scaleValueTo(value, unitPrefix, down),
                    down,
                    scale
            );
        }
        return this;
    }


    @Override
    public boolean isLowerScaleThan(ScaledNumber scaledNumber) {
        return getUnitPrefix().isLowerScaleThan(scaledNumber.getUnitPrefix());
    }

    @Override
    public boolean isHigherScaleThan(ScaledNumber scaledNumber) {
        return getUnitPrefix().isHigherScaleThan(scaledNumber.getUnitPrefix());
    }

    @Override
    public boolean hasUp() {
        return getUnitPrefix().hasUp();
    }

    @Override
    public boolean hasDown() {
        return getUnitPrefix().hasDown();
    }

    @Override
    public String toString() {
        return getValue()
                .toPlainString();
    }

    public String toPrefixString() {
        return normalize(getValue())
                .toPlainString()
                + " "
                + getUnitPrefix().getSymbol();
    }

    public String toFullPrefixString() {
        return normalize(getValue())
                .toPlainString()
                + " "
                + getUnitPrefix().getName().toLowerCase();
    }

    public ScaledNumber normalize() {
        return this;
    }

    public Comparator<UnitPrefix> givenValue(ScaledNumber scaledNumber) {
        if (scaledNumber.getValue().precision() > 3) {
            return Comparator.comparing(up -> Math.abs(up.getPowerOfTen() - scaledNumber.getValue().precision()));
        } else if (scaledNumber.getValue().precision() == 0 && scaledNumber.getValue().scale() > 3) {



            return Comparator.comparing(up -> Math.abs(up.getPowerOfTen() - scaledNumber.getValue().precision()));
        }
        return null;
    }
}
