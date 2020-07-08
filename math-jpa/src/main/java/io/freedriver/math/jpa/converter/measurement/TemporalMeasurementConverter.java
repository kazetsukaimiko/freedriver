package io.freedriver.math.jpa.converter.measurement;

import io.freedriver.math.UnitPrefix;
import io.freedriver.math.measurement.types.TemporalMeasurement;

import java.math.BigDecimal;

public abstract class TemporalMeasurementConverter<TM extends TemporalMeasurement<TM>> extends MeasurementConverter<TM> {
    @Override
    public BigDecimal convertToDatabaseColumn(TM tm) {
        return tm.scaleTo(UnitPrefix.ONE).getValue()
                .getValue();
    }

    @Override
    public TM convertToEntityAttribute(BigDecimal value) {
        return value != null
                ? construct(value, UnitPrefix.ONE).normalize()
                : null;

    }

    protected abstract TM construct(BigDecimal value, UnitPrefix unitPrefix);
}
