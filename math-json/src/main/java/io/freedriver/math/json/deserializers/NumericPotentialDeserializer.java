package io.freedriver.math.json.deserializers;

import io.freedriver.math.measurement.types.electrical.Potential;
import io.freedriver.math.number.ScaledNumber;

public class NumericPotentialDeserializer extends MeasurementDeserializer<Potential> {
    @Override
    protected Potential read(ScaledNumber s) {
        return new Potential(s);
    }
}
