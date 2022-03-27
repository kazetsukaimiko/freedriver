package io.freedriver.base.util.accumulator;

import java.util.function.Function;

public interface Accumulator<I, R> {
    boolean isComplete(I input);
    R convert(I input);

    default  <RT> Accumulator<I, RT> map(Function<R, RT> mapper) {
        return new MappingAccumulator<>(this, mapper);
    }
}
