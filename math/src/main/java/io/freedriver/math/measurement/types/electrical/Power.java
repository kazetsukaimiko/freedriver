package io.freedriver.math.measurement.types.electrical;

import io.freedriver.math.measurement.types.InstantaneousMeasurement;
import io.freedriver.math.measurement.types.creators.ToTemporal;
import io.freedriver.math.measurement.units.SIElectricalUnit;
import io.freedriver.math.number.ScaledNumber;

/**
 * Measurement in Watts.
 */
public class Power extends InstantaneousMeasurement<Power> implements ToTemporal<Energy> {
    public Power(ScaledNumber number) {
        super(number, SIElectricalUnit.WATTS);
    }

    public Power() {
    }

    @Override
    protected Power construct(ScaledNumber value) {
        return new Power(value);
    }

    @Override
    public Energy hours(Number multiplier) {
        return new Energy(getValue().multiply(ScaledNumber.of(multiplier)));
    }
}
