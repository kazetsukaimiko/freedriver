package io.freedriver.math.measurement.types.creators;

import io.freedriver.math.measurement.types.TemporalMeasurement;

public interface ToTemporal<M extends TemporalMeasurement<M>> {
    M hours(Number multiplier);

    default M hours() {
        return hours(1);
    }
}
