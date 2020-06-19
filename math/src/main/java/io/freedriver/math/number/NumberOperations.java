package io.freedriver.math.number;

public interface NumberOperations<T extends Number> extends Comparable<T> {
    T add(ScaledNumber other);
    default T add(Number n) {
        return add(ScaledNumber.of(n));
    }
    // T add(T other, MathContext mc);

    T subtract(ScaledNumber other);
    default T subtract(Number n) {
        return subtract(ScaledNumber.of(n));
    }
    // T subtract(T other, MathContext mc);

    T multiply(ScaledNumber other);
    default T multiply(Number n) {
        return multiply(ScaledNumber.of(n));
    }
    // T multiply(T other, MathContext mc);

    T divide(ScaledNumber other);
    default T divide(Number n) {
        return divide(ScaledNumber.of(n));
    }

    T abs();
    // T divide(T other, MathContext mc);
    // T divide(T other, RoundingMode roundingMode, MathContext mc);
}
