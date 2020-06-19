package io.freedriver.math.measurement.types;

import io.freedriver.math.TemporalUnit;
import io.freedriver.math.measurement.units.SIElectricalUnit;
import io.freedriver.math.number.ScaledNumber;

import java.util.Objects;

public abstract class TemporalMeasurement<M extends TemporalMeasurement<M>> extends Measurement<M>  {
    private TemporalUnit temporalUnit;
    public TemporalMeasurement(ScaledNumber value, SIElectricalUnit SIUnit, TemporalUnit temporalUnit) {
        super(value, SIUnit);
        this.temporalUnit = temporalUnit;
    }

    public TemporalMeasurement() {
    }

    public TemporalUnit getTemporalUnit() {
        return temporalUnit;
    }

    public void setTemporalUnit(TemporalUnit temporalUnit) {
        this.temporalUnit = temporalUnit;
    }

    @Override
    public String getFullUnit() {
        return getValue().toPrefixString()
                + getUnit().getSymbol()
                + getTemporalUnit().getSuffix();
    }

    @Override
    public String getFullUnitName() {
        return getValue().toFullPrefixString()
                + getUnit().name().toLowerCase()
                + " "
                + getTemporalUnit().getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TemporalMeasurement<?> that = (TemporalMeasurement<?>) o;
        return temporalUnit == that.temporalUnit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), temporalUnit);
    }
}
