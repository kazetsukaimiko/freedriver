package io.freedriver.serial;

import io.freedriver.base.util.ByteArrayBuilder;
import io.freedriver.base.util.UncheckedCloseable;
import io.freedriver.serial.api.exception.SerialResourceException;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Internal helper class to get Lines from the VE.Direct interface. Read-only. You probably don't want to use this-
 * Instead use VEDirectMessage.openStream(accumulator).
 */
public class SerialListener<T> implements Iterable<T>, Iterator<T>, AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(SerialListener.class.getName());
    private static final byte DELIMITER = '\n';

    private final SerialResource serialResource;
    private final Function<SerialListener<T>, T> accumulator;


    public SerialListener(SerialResource serialResource, Function<SerialListener<T>, T> accumulator) {
        this.serialResource = serialResource;
        this.accumulator = accumulator;
    }

    /*
     * Iterator/Iterable methods:
     */
    @Override
    public boolean hasNext() {
        return serialResource.isOpened();
    }

    @Override
    public T next() {
        return accumulator.apply(this);
    }

    public String write(byte[] buffer) {
        serialResource.clear();
        serialResource.write(buffer);
        return nextLine();
    }


    public byte[] nextLineByteArray() {
        ByteArrayBuilder bab = new ByteArrayBuilder();
        while (serialResource.hasNext()) {
            Byte current = serialResource.nextByte();
            if (!Objects.equals(current, DELIMITER)) {
                bab.append(current);
            } else {
                break;
            }
        }
        return bab.build();
    }

    public String nextLine() {
        StringBuilder sb = new StringBuilder();
        while (serialResource.hasNext()) {
            String character = serialResource.next();

            if (!Objects.equals(character, "\n")) {
                sb.append(character);
            } else {
                break;
            }
        }
        if (!serialResource.hasNext()) {
            try {
                serialResource.close();
            } catch (Exception e) {
                throw new SerialResourceException("Couldn't close", e);
            }
        }
        return sb.toString();
    }

    @Override
    public Iterator<T> iterator() {
        serialResource.iterator(); // Reset serialResource iteratble.
        return this;
    }

    /*
     * Method to stream output
     */
    public Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false)
                .onClose(UncheckedCloseable.wrap(this));
    }

    @Override
    public void close() throws Exception {
        LOGGER.info("Closing resource: " + serialResource.getName());
        serialResource.close();
    }
}
