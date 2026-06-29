package io.freedriver.math.jpa.converter.measurement;

import java.math.BigDecimal;

import io.freedriver.math.UnitPrefix;
import io.freedriver.math.measurement.types.thermo.Temperature;
import io.freedriver.math.measurement.units.TemperatureScale;
import io.freedriver.math.number.ScaledNumber;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TemperatureConverter extends MeasurementConverter<Temperature> implements AttributeConverter<Temperature, BigDecimal> {
    @Override
    protected Temperature construct(BigDecimal value, UnitPrefix unitPrefix) {
        return new Temperature(ScaledNumber.of(value, unitPrefix), TemperatureScale.CELSUIS);
    }
}
