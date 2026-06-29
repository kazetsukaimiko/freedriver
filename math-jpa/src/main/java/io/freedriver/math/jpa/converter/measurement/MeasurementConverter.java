package io.freedriver.math.jpa.converter.measurement;

import java.math.BigDecimal;

import io.freedriver.math.UnitPrefix;
import io.freedriver.math.measurement.types.Measurement;
import jakarta.persistence.AttributeConverter;

public abstract class MeasurementConverter<M extends Measurement<M>> implements AttributeConverter<M, BigDecimal> {
    protected abstract M construct(BigDecimal value, UnitPrefix unitPrefix);

    @Override
    public BigDecimal convertToDatabaseColumn(M m) {
        return m != null ? m.scaleTo(UnitPrefix.ONE).getValue()
                .getValue()
                : null;
    }

    @Override
    public M convertToEntityAttribute(BigDecimal value) {
        return value != null
                ? construct(value, UnitPrefix.ONE)
                    .normalize()
                : null;
    }
}
