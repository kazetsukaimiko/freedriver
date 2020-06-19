package io.freedriver.math.number;

public interface Scaled<S extends Scaled<S, C>, C> extends Scaleable<S> {

    /**
     * Gets the underlying backing value.
     */
    C getValue();

}
