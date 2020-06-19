package io.freedriver.math.measurement.types.electrical;

import io.freedriver.math.TemporalUnit;
import io.freedriver.math.measurement.types.TemporalMeasurement;
import io.freedriver.math.measurement.units.SIElectricalUnit;
import io.freedriver.math.number.ScaledNumber;

/**
 * Measurement in Watt-Hours.
 *  TODO : Temporal Conversion
 */
public class Energy extends TemporalMeasurement<Energy> {

    // TODO: Time Multiplier
    // private TemporalMultiplier temporalMultiplier

    public Energy(ScaledNumber value) {
        super(value, SIElectricalUnit.WATTS, TemporalUnit.HOURS);
    }

    public Energy() {
    }

    @Override
    protected Energy construct(ScaledNumber value) {
        return new Energy(value);
    }
}
