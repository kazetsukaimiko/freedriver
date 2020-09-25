package io.freedriver.math.measurement.types.electrical;

import io.freedriver.math.measurement.types.InstantaneousMeasurement;
import io.freedriver.math.measurement.units.SIElectricalUnit;
import io.freedriver.math.number.ScaledNumber;

/**
 * Measurement in Amps.
 */
public class Current extends InstantaneousMeasurement<Current> {
    public Current(ScaledNumber value) {
        super(value, SIElectricalUnit.AMPS);
    }

    public Current() {
    }

    @Override
    protected Current construct(ScaledNumber value) {
        return new Current(value);
    }

    public Power toPower(Potential v) {
        return new Power(multiply(v).getValue());
    }
}
