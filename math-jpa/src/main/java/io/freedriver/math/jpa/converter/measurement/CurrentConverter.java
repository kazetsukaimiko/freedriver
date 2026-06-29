package io.freedriver.math.jpa.converter.measurement;

import java.math.BigDecimal;

import io.freedriver.math.UnitPrefix;
import io.freedriver.math.measurement.types.electrical.Current;
import io.freedriver.math.number.ScaledNumber;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CurrentConverter extends MeasurementConverter<Current> implements AttributeConverter<Current, BigDecimal> {
    @Override
    protected Current construct(BigDecimal value, UnitPrefix unitPrefix) {
        return new Current(ScaledNumber.of(value, unitPrefix));
    }
}
