package io.freedriver.victron;

import io.freedriver.serial.stream.api.BaseStreamListener;
import io.freedriver.serial.stream.api.SerialStream;
import io.freedriver.serial.stream.api.StreamListener;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Represents a VE.Direct Device. A VE.Direct device is NOT the connected device, eg. an MPPT Charge controller.
 * A VE.Direct device is the link to the Victron Product. If you want to detect the product, use:
 * {attemptToGetProduct}
 */
public class VEDirectStreamer extends BaseStreamListener<VEDirectMessage> implements StreamListener<VEDirectMessage> {
    private static final Logger LOGGER = Logger.getLogger(VEDirectStreamer.class.getName());
    private static final Path BY_ID_PATH = Paths.get("/dev/serial/by-id/");
    private static final MessageAccumulator SOURCE = new MessageAccumulator();
    private final VEDirectMessage firstMessage;

    public VEDirectStreamer(SerialStream stream) {
        super(stream, SOURCE);
        // Do this to empty the serial buffer
        try {
            firstMessage = SOURCE.apply(stream);
        } catch (IOException e) {
            // TODO
            throw new RuntimeException(e);
        }
    }

    public VEDirectMessage getFirstMessage() {
        return firstMessage;
    }

    public static Stream<VEDirectStreamer> allMessageStreamers() throws IOException {
        return allMessageStreamers(p -> true);
    }

    public static Stream<VEDirectStreamer> allMessageStreamers(Predicate<Path> pathFilter) throws IOException {
        return VictronLink.allSerialStreams(pathFilter)
                .map(VEDirectStreamer::new);
    }

}
