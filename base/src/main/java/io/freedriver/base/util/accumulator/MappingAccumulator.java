package io.freedriver.base.util.accumulator;

import java.util.function.Function;

public class MappingAccumulator<I, R, M> implements Accumulator<I, M> {
    private final Accumulator<I, R> delegate;
    private final Function<R, M> mapper;

    public MappingAccumulator(Accumulator<I, R> delegate, Function<R, M> mapper) {
        this.delegate = delegate;
        this.mapper = mapper;
    }

    @Override
    public boolean isComplete(I input) {
        return delegate.isComplete(input);
    }

    @Override
    public M convert(I input) {
        return mapper.apply(delegate.convert(input));
    }
}
