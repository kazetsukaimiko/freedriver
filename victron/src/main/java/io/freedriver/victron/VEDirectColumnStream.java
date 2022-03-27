package io.freedriver.victron;

import io.freedriver.serial.api.SerialResource;
import io.freedriver.serial.stream.api.SerialEntityStream;
import io.freedriver.serial.stream.api.accumulators.NewlineAccumulator;

import java.util.Iterator;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents a VE.Direct Device. A VE.Direct device is NOT the connected device, eg. an MPPT Charge controller.
 * A VE.Direct device is the link to the Victron Product. If you want to detect the product, use:
 * {attemptToGetProduct}
 */
public class VEDirectColumnStream implements Iterable<VEDirectColumnValue> {
    private static final Logger LOGGER = Logger.getLogger(VEDirectColumnStream.class.getName());

    private final Iterator<String> lineIterator;

    public VEDirectColumnStream(SerialResource serialResource) {
        lineIterator = new SerialEntityStream<>(serialResource, NewlineAccumulator.INSTANCE);
    }

    @Override
    public Iterator<VEDirectColumnValue> iterator() {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return lineIterator.hasNext();
            }

            @Override
            public VEDirectColumnValue next() {
                String line = lineIterator.next();
                Optional<VEDirectColumnValue> column = VEDirectColumnValue.fromSerial(line);
                while (column.isEmpty() && hasNext()) {
                    line = lineIterator.next();
                    column = VEDirectColumnValue.fromSerial(line);
                }
                return column.orElseThrow(() -> new RuntimeException("Unable to get VEDirectColumnValue."));
            }
        };
    }

    public Stream<VEDirectColumnValue> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
