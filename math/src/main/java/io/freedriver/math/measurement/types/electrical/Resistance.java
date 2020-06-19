package io.freedriver.math.measurement.types.electrical;

import io.freedriver.math.measurement.types.FixedMeasurement;
import io.freedriver.math.measurement.units.SIElectricalUnit;
import io.freedriver.math.number.ScaledNumber;

/**
 * Measurement in Ohms.
 */
public class Resistance extends FixedMeasurement<Resistance> {
    public Resistance(ScaledNumber number) {
        super(number, SIElectricalUnit.OHMS);
    }

    public Resistance() {
    }

    @Override
    protected Resistance construct(ScaledNumber value) {
        return new Resistance(value);
    }
}
