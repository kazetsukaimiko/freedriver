package io.freedriver.math.measurement.types.electrical;

import io.freedriver.math.measurement.types.FixedMeasurement;
import io.freedriver.math.measurement.units.SIElectricalUnit;
import io.freedriver.math.number.ScaledNumber;

/**
 * Measurement in Volts.
 */
public class Potential extends FixedMeasurement<Potential> {
    public Potential(ScaledNumber number) {
        super(number, SIElectricalUnit.VOLTS);
    }

    public Potential() {
    }

    @Override
    protected Potential construct(ScaledNumber value) {
        return new Potential(value);
    }

    public Power toPower(Current c) {
        return new Power(multiply(c).getValue());
    }
}
