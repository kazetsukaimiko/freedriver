package io.freedriver.math;

import io.freedriver.math.measurement.types.electrical.Potential;
import io.freedriver.math.number.ScaledNumber;

public class PotentialTest extends MeasurementTest<Potential> {

    @Override
    protected Potential construct(ScaledNumber scaledNumber) {
        return new Potential(scaledNumber);
    }
}
