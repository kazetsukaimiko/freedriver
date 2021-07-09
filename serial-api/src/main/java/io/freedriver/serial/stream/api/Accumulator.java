package io.freedriver.serial.stream.api;

import java.io.IOException;
import java.util.function.Function;

public interface Accumulator<T> {
    T apply(SerialStream stream) throws IOException;
    default <X> Accumulator<X> map(Function<T, X> mapper) {
        return (s -> mapper.apply(apply(s)));
    }
}
