package io.freedriver.math;

import io.freedriver.math.measurement.types.electrical.Power;
import io.freedriver.math.number.ScaledNumber;

public class PowerTest extends MeasurementTest<Power> {

    @Override
    protected Power construct(ScaledNumber scaledNumber) {
        return new Power(scaledNumber);
    }
}
