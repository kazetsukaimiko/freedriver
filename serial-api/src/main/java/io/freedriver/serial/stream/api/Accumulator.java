package io.freedriver.serial.stream.api;

import io.freedriver.serial.stream.api.accumulators.MappingAccumulator;

import java.util.function.Function;

public interface Accumulator<I, R> {
    boolean isComplete(I input);
    R convert(I input);

    default  <RT> Accumulator<I, RT> map(Function<R, RT> mapper) {
        return new MappingAccumulator<>(this, mapper);
    }
}
