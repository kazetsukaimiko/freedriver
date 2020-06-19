package io.freedriver.victron;

import io.freedriver.victron.vedirect.OffReason;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EnumsTest {

    private static final Logger LOGGER = Logger.getLogger(EnumsTest.class.getName());

    @Test
    public void testOffReason() {
        Stream.of(OffReason.values())
                .forEach(offReason -> assertEquals(offReason, OffReason.byReasonCode(offReason.getReasonCode()).orElse(null)));
    }

    @Test
    public void testErrorCode() {
        Stream.of(ErrorCode.values())
                .forEach(errorCode -> {
                    assertNotNull(errorCode.getDescription());
                    assertEquals(errorCode, ErrorCode.byCode(errorCode.getCode()).orElse(null));
                    assertEquals(errorCode, ErrorCode.byName(errorCode.name()).orElse(null));
                });
    }

    @Test
    public void testLoadOutputState() {
        Stream.of(LoadOutputState.values())
                .forEach(loadOutputState -> assertEquals(loadOutputState, LoadOutputState.byName(loadOutputState.name()).orElse(null)));
    }

    @Test
    public void testRelayState() {
        Stream.of(RelayState.values())
                .forEach(relayState ->
                        assertEquals(relayState, RelayState.byName(relayState.name()).orElse(null)));
    }

    @Test
    public void testStateOfOperation() {
        Stream.of(StateOfOperation.values())
                .forEach(stateOfOperation -> {
                    assertNotNull(stateOfOperation.getDescription());
                    assertEquals(stateOfOperation, StateOfOperation.byName(stateOfOperation.name()).orElse(null));
                    assertEquals(stateOfOperation, StateOfOperation.byCode(stateOfOperation.getCode()).orElse(null));
                });
    }

    @Test
    public void testTrackerOperation() {
        Stream.of(TrackerOperation.values())
                .forEach(trackerOperation -> {
                    LOGGER.info(trackerOperation.getDescription());
                    assertEquals(trackerOperation, TrackerOperation.byName(trackerOperation.name()).orElse(null));
                    assertEquals(trackerOperation, TrackerOperation.byCode(trackerOperation.getCode()).orElse(null));
                });
    }


    @Test
    public void testVictronProduct() {
        Stream.of(VictronProduct.values())
                .forEach(victronProduct -> {
                    assertEquals(victronProduct, VictronProduct.byProductId(victronProduct.getProductId()).orElse(null));
                });
    }

}
