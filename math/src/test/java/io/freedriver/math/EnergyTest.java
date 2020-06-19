package io.freedriver.math;

import io.freedriver.math.measurement.types.electrical.Energy;
import io.freedriver.math.number.ScaledNumber;

public class EnergyTest extends TemporalMeasurementTest<Energy> {

    @Override
    protected Energy construct(ScaledNumber scaledNumber) {
        return new Energy(scaledNumber);
    }
}
