package io.freedriver.math.number;

public interface Scaleable<S> {
    /**
     * Reference to this, typed.
     */
    S me();

    /**
     * Get our scalable at a higher unit.
     */
    S up();

    /**
     * Get our scalable at a higher unit.
     */
    S down();

    /**
     * Is our scalable a lower unit than s
     */
    boolean isLowerScaleThan(S s);

    /**
     * Is our scalable a higher unit than s
     */
    boolean isHigherScaleThan(S s);

    /**
     * Do we have a unit above our own in scale
     */
    boolean hasUp();

    /**
     * Do we have a unit below our own in scale
     */
    boolean hasDown();

    /**
     * CompareTo using Scale
     */
    int scaleCompareTo(S s);
}
