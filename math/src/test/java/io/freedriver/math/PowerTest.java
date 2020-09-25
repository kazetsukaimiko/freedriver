package io.freedriver.math;

import io.freedriver.math.measurement.types.electrical.Current;
import io.freedriver.math.measurement.types.electrical.Potential;
import io.freedriver.math.measurement.types.electrical.Power;
import io.freedriver.math.number.ScaledNumber;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PowerTest extends MeasurementTest<Power> {

    @Override
    protected Power construct(ScaledNumber scaledNumber) {
        return new Power(scaledNumber);
    }

    @Test
    public void testConversionFromPotentialAndCurrent() {
        Current one = new Current(ScaledNumber.ONE);
        Potential ten = new Potential(ScaledNumber.TEN);
        Power power = new Power(ScaledNumber.TEN);
        assertEquals(one.toPower(ten), ten.toPower(one));
        assertEquals(power, one.toPower(ten));
        assertEquals(power, ten.toPower(one));
    }
}
