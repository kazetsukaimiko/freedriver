package io.freedriver.base.util.accumulator;

import io.freedriver.serial.stream.api.SerialEntityStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public abstract class ByteArrayAccumulatorsTest<R, A extends Accumulator<ByteArrayOutputStream, R>> extends AccumulatorsTest<R, ByteArrayOutputStream, A> {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    protected SerialEntityStream<R> serialEntityStream;
    protected Stream<R> javaStream;
    protected Future<Boolean> worker;
    protected final List<R> readItems = new ArrayList<>();

    @BeforeEach
    public void init() {
        serialEntityStream = new SerialEntityStream<>(resource, accumulator);
        javaStream = serialEntityStream.stream();
        worker = executor.submit(() -> {
            javaStream.forEach(readItems::add);
            return true;
        });
    }

    @AfterEach
    public void destroy() {
        if (worker != null && !worker.isCancelled() && !worker.isDone()) {
            worker.cancel(true);
        }
    }

    @Override
    protected R read() {
        while (readItems.isEmpty()) {
            waitABit();
            if (worker.isDone() || worker.isCancelled()) {
                return null;
            }
        }
        return readItems.remove(0);
    }
}
