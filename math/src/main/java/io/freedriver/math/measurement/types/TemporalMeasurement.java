package io.freedriver.math.measurement.types;

import java.util.Objects;

import io.freedriver.math.TemporalUnit;
import io.freedriver.math.measurement.units.SIElectricalUnit;
import io.freedriver.math.number.ScaledNumber;
import lombok.Getter;

@Getter
public abstract class TemporalMeasurement<M extends TemporalMeasurement<M>> extends Measurement<M>  {
    private TemporalUnit temporalUnit;
    public TemporalMeasurement(ScaledNumber value, SIElectricalUnit SIUnit, TemporalUnit temporalUnit) {
        super(value, SIUnit);
        this.temporalUnit = temporalUnit;
    }

    public TemporalMeasurement() {
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
