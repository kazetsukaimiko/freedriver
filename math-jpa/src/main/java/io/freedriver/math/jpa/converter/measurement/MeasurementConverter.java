package io.freedriver.math.jpa.converter.measurement;

import io.freedriver.math.UnitPrefix;
import io.freedriver.math.measurement.types.Measurement;

import javax.persistence.AttributeConverter;
import java.math.BigDecimal;

public abstract class MeasurementConverter<M extends Measurement<M>> implements AttributeConverter<M, BigDecimal> {
    public abstract M construct(BigDecimal value, UnitPrefix unitPrefix);

    @Override
    public BigDecimal convertToDatabaseColumn(M m) {
        return m != null ? m.scaleTo(UnitPrefix.ONE).getValue()
                .getValue()
                : null;
    }

    @Override
    public M convertToEntityAttribute(BigDecimal value) {
        return construct(value, UnitPrefix.ONE)
                .normalize();
    }
}
