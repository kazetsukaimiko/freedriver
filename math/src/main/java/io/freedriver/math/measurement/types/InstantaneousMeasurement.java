package io.freedriver.math.measurement.types;

import io.freedriver.math.measurement.units.Unit;
import io.freedriver.math.number.ScaledNumber;

/**
 * A Measurement lacking a temporal component but capable of having one.
 * @param <M>
 */
public abstract class InstantaneousMeasurement<M extends InstantaneousMeasurement<M>> extends Measurement<M>  {
    protected InstantaneousMeasurement(ScaledNumber number,  Unit unit) {
        super(number, unit);
    }

    public InstantaneousMeasurement() {
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
