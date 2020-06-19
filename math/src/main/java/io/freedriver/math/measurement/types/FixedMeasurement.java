package io.freedriver.math.measurement.types;

import io.freedriver.math.measurement.units.Unit;
import io.freedriver.math.number.ScaledNumber;

/**
 * A Measurement lacking a temporal component, incapable of having one.
 * @param <M>
 */
public abstract class FixedMeasurement<M extends FixedMeasurement<M>> extends Measurement<M>  {
    protected FixedMeasurement(ScaledNumber number, Unit unit) {
        super(number, unit);
    }

    public FixedMeasurement() {
    }

    @Override
    public String getFullUnit() {
        return getValue().toPrefixString()
                + getUnit().getSymbol();
    }

    @Override
    public String getFullUnitName() {
        return getValue().toFullPrefixString()
                + getUnit().name().toLowerCase();
    }
}
