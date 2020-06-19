package io.freedriver.math;

import io.freedriver.math.measurement.types.electrical.Current;
import io.freedriver.math.number.ScaledNumber;

public class CurrentTest extends MeasurementTest<Current> {

    @Override
    protected Current construct(ScaledNumber scaledNumber) {
        return new Current(scaledNumber);
    }
}
