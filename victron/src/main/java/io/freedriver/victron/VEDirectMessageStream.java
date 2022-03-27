package io.freedriver.victron;

import io.freedriver.serial.api.SerialResource;

import java.util.Iterator;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents a VE.Direct Device. A VE.Direct device is NOT the connected device, eg. an MPPT Charge controller.
 * A VE.Direct device is the link to the Victron Product. If you want to detect the product, use:
 * {attemptToGetProduct}
 */
public class VEDirectMessageStream implements Iterable<VEDirectMessage> {
    private static final Logger LOGGER = Logger.getLogger(VEDirectMessageStream.class.getName());

    private final Iterable<VEDirectColumnValue> columnValues;

    public VEDirectMessageStream(SerialResource serialResource) {
        columnValues = new VEDirectColumnStream(serialResource);
    }

    @Override
    public Iterator<VEDirectMessage> iterator() {
        final Iterator<VEDirectColumnValue> columnValueIterator = columnValues.iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return columnValueIterator.hasNext();
            }

            @Override
            public VEDirectMessage next() {
                VEDirectMessage message = new VEDirectMessage();
                VEDirectColumnValue columnValue = columnValueIterator.next();
                while (columnValue.getColumn() != VEDirectColumn.CHECKSUM) {
                    columnValue.apply(message);
                    columnValue = columnValueIterator.next();
                }
                return message;
            }
        };
    }

    public Stream<VEDirectMessage> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
