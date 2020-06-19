package io.freedriver.math.jpa.converter.measurement;

import io.freedriver.math.measurement.types.electrical.Potential;

public class PotentialConverterTest extends MeasurementConverterTest<Potential, PotentialConverter> {
    @Override
    public PotentialConverter spawn() {
        return new PotentialConverter();
    }
}
