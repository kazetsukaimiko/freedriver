package io.freedriver.math.measurement.types;

import io.freedriver.math.UnitPrefix;
import io.freedriver.math.measurement.units.Unit;
import io.freedriver.math.number.NumberDelegate;
import io.freedriver.math.number.NumberOperations;
import io.freedriver.math.number.Scaleable;
import io.freedriver.math.number.ScaledNumber;

import java.util.Objects;

/**
 * Class to handle (Instantaneous) Measurements of Power and Current.
 */
public abstract class Measurement<M extends Measurement<M>> extends NumberDelegate implements Scaleable<M>, NumberOperations<M> {
    private ScaledNumber value;
    private Unit unit;

    protected Measurement(ScaledNumber value, Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    public Measurement() {
    }

    /**
     * Makes an instance of this measurement type.
     */
    protected abstract M construct(ScaledNumber value);

    /**
     * Returns the full Unit with Multiplier. e.g. KILO + WATTS = kW.
     * @return
     */

    public abstract String getFullUnit();

    /**
     * Returns the full Unit with Multiplier name. e.g. KILO + WATTS = KILOWATTS.
     * @return
     */
    public abstract String getFullUnitName();

    public ScaledNumber getValue() {
        return value;
    }

    public void setValue(ScaledNumber value) {
        this.value = value;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @Override
    public Number getDelegateNumber() {
        return value;
    }


    @Override
    public M me() {
        return construct(value);
    }

    @Override
    public M up() {
        return construct(value.up());
    }

    @Override
    public M down() {
        return construct(value.down());
    }

    @Override
    public boolean isLowerScaleThan(M m) {
        return value.isLowerScaleThan(m.getValue());
    }

    @Override
    public boolean isHigherScaleThan(M m) {
        return value.isHigherScaleThan(m.getValue());
    }

    @Override
    public boolean hasUp() {
        return value.hasUp();
    }

    @Override
    public boolean hasDown() {
        return value.hasDown();
    }

    @Override
    public int compareTo(M m) {
        return value.compareTo(m.getValue());
    }

    @Override
    public int scaleCompareTo(M m) {
        return value.getUnitPrefix().compareTo(m.getValue().getUnitPrefix());
    }

    @Override
    public M add(ScaledNumber other) {
        return construct(value.add(other));
    }

    @Override
    public M subtract(ScaledNumber other) {
        return construct(value.subtract(other));
    }

    @Override
    public M multiply(ScaledNumber other) {
        return construct(value.multiply(other));
    }

    @Override
    public M divide(ScaledNumber other) {
        return construct(value.divide(other));
    }

    @Override
    public M abs() {
        return construct(value.abs());
    }

    public M scaleTo(UnitPrefix unitPrefix) {
        return construct(value.scaleTo(unitPrefix));
    }

    public M normalize() {
        return construct(value.normalize());
    }

    public boolean lessThan(M other) {
        return compareTo(other) < 0;
    }

    public boolean greaterThan(M other) {
        return compareTo(other) > 0;
    }

    public boolean lessThanOrEqualTo(M other) {
        return compareTo(other) <= 0;
    }

    public boolean greaterThanOrEqualTo(M other) {
        return compareTo(other) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Measurement<?> that = (Measurement<?>) o;
        return value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, unit);
    }

    @Override
    public String toString() {
        return getFullUnit();
    }
}
