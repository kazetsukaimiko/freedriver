package io.freedriver.math.measurement;

import io.freedriver.math.measurement.types.Measurement;
import io.freedriver.math.number.NumberOperations;

public interface MeasurementOperations<M extends Measurement<M>> extends NumberOperations<M> {
    default M add(M other) {
        return add(other.getValue());
    }
    // T add(T other, MathContext mc);

    default M subtract(M other) {
        return subtract(other.getValue());
    }
    // T subtract(T other, MathContext mc);

    default M multiply(M other) {
        return multiply(other.getValue());
    }
    // T multiply(T other, MathContext mc);

    default M divide(M other) {
        return divide(other.getValue());
    }

}
