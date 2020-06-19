package io.freedriver.math.measurement.types.thermo;

import io.freedriver.math.measurement.types.FixedMeasurement;
import io.freedriver.math.measurement.units.TemperatureScale;
import io.freedriver.math.number.ScaledNumber;

public class Temperature extends FixedMeasurement<Temperature> {
    public Temperature(ScaledNumber number, TemperatureScale unit) {
        super(number, unit);
    }

    public Temperature() {
    }

    @Override
    protected Temperature construct(ScaledNumber value) {
        return new Temperature(value, TemperatureScale.CELSUIS);
    }
}