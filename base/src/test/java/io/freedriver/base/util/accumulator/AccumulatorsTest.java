package io.freedriver.base.util.accumulator;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AccumulatorsTest<R, I, A extends Accumulator<I, R>> {
    private static final Duration DEFAULT_DURATION = Duration.ofMillis(20);

    protected abstract Accumulator<I, R> accumulator();
    protected abstract void write(R item) throws IOException;
    protected abstract R random();
    protected abstract R read();

    Accumulator<I, R> accumulator = accumulator();

    @Test
    public void test() throws IOException {
        R rand = random();
        write(rand);
        R read = read();
        assertEquals(rand, read);
    }


    public void waitFor(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void waitABit() {
        waitFor(DEFAULT_DURATION);
    }

}
