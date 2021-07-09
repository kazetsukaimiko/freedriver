package io.freedriver.serial.stream.api;

import io.freedriver.serial.api.AutoCloseableWithState;
import io.freedriver.serial.api.exception.SerialStreamException;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface StreamListener<T> extends Iterable<T>, Iterator<T>, AutoCloseableWithState {
    SerialStream getStream();
    long countRead();

    /**
     * Create a Stream, with the lifecycle of the Stream tied to the SerialStream.
     */
    default Stream<T> stream() {
        return StreamSupport
                .stream(spliterator(), false)
                .takeWhile(t -> !isClosed())
                .onClose(() -> {
                    try {
                        close();
                    } catch (Exception e) {
                        throw new SerialStreamException("Exception closing SerialStream: ", e);
                    }
                });
    }


    default T readUntil(Predicate<T> predicate) {
        return readUntil(predicate, t -> {});
    }

    default T readUntil(Predicate<T> predicate, Consumer<T> reducer) {
        while (!isClosed()) {
            T current = next();
            reducer.accept(current);
            if (predicate.test(current)) {
                return current;
            }
        }
        return null;
    }
}
