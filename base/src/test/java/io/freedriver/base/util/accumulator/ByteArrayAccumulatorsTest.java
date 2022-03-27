package io.freedriver.base.util.accumulator;

import io.freedriver.base.util.EntityStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public abstract class ByteArrayAccumulatorsTest<R, A extends Accumulator<ByteArrayOutputStream, R>> extends AccumulatorsTest<R, ByteArrayOutputStream, A> {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    protected PipedOutputStream outputStream;
    protected PipedInputStream inputStream;
    protected EntityStream<R> entityStream;
    protected Stream<R> javaStream;
    protected Future<Boolean> worker;
    protected final List<R> readItems = new ArrayList<>();

    @BeforeEach
    public void init() throws IOException {
        outputStream = new PipedOutputStream();
        inputStream = new PipedInputStream(outputStream);
        entityStream = new EntityStream<>(inputStream, accumulator);
        javaStream = entityStream.stream();
    }

    @AfterEach
    public void destroy() throws IOException {
        inputStream.close();
        outputStream.close();
    }

    @Override
    protected R read() {
        return entityStream.next();
    }
}
