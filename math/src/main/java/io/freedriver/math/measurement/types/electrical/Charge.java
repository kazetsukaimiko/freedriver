package io.freedriver.math.measurement.types.electrical;

import io.freedriver.math.measurement.types.FixedMeasurement;
import io.freedriver.math.measurement.units.SIElectricalUnit;
import io.freedriver.math.number.ScaledNumber;

/**
 * Measurement in Volts.
 */
public class Charge extends FixedMeasurement<Charge> {
    public Charge(ScaledNumber value) {
        super(value, SIElectricalUnit.COULOMBS);
    }

    public Charge() {
    }

    @Override
    protected Charge construct(ScaledNumber value) {
        return new Charge(value);
    }
}
