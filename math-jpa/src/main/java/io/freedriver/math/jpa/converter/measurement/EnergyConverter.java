package io.freedriver.math.jpa.converter.measurement;

import io.freedriver.math.UnitPrefix;
import io.freedriver.math.measurement.types.electrical.Energy;
import io.freedriver.math.number.ScaledNumber;

import jakarta.persistence.Converter;
import java.math.BigDecimal;

@Converter(autoApply = true)
public class EnergyConverter extends TemporalMeasurementConverter<Energy> {

    @Override
    protected Energy construct(BigDecimal value, UnitPrefix unitPrefix) {
        return new Energy(ScaledNumber.of(value, unitPrefix));
    }
}
