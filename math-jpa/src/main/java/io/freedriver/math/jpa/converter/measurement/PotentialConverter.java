package io.freedriver.math.jpa.converter.measurement;

import java.math.BigDecimal;

import io.freedriver.math.UnitPrefix;
import io.freedriver.math.measurement.types.electrical.Potential;
import io.freedriver.math.number.ScaledNumber;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PotentialConverter extends MeasurementConverter<Potential> {
    @Override
    protected Potential construct(BigDecimal value, UnitPrefix unitPrefix) {
        return new Potential(ScaledNumber.of(value, unitPrefix));
    }
}
