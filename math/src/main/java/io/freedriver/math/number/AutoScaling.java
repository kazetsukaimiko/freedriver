package io.freedriver.math.number;

import java.util.stream.Stream;

public interface AutoScaling<S extends AutoScaling<S>> extends Scaleable<S> {
    /**
     * Get our scalable at all units.
     */
    Stream<S> all();

    @Override
    default S up() {
        return all()
                .filter(this::isLowerScaleThan)
                .min(S::scaleCompareTo)
                .orElseGet(this::me);
    }

    @Override
    default S down() {
        return all()
                .filter(this::isHigherScaleThan)
                .max(S::scaleCompareTo)
                .orElseGet(this::me);
    }

    @Override
    default boolean isHigherScaleThan(S s) {
        return scaleCompareTo(s) > 0;
    }

    @Override
    default boolean isLowerScaleThan(S s) { return scaleCompareTo(s) < 0; }
}
