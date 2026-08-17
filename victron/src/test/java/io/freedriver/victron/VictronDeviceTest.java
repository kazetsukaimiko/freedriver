package io.freedriver.victron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class VictronDeviceTest {
    private static final Logger LOGGER = Logger.getLogger(VictronDeviceTest.class.getName());

    @Test
    public void testVictronProduct() {
        Set<VictronDevice> victronDevices = new HashSet<>();

        Stream.of(UUID.randomUUID().toString(), UUID.randomUUID().toString())
                .forEach(serial -> {
            Stream.of(VictronProduct.values())
                    .map(type -> VictronDevice.builder()
                            .serialNumber(serial)
                            .type(type)
                            .build())
                    .peek(vp -> LOGGER.info(vp.toString()))
                    .forEach(vp -> {
                        VEDirectMessage veDirectMessage = VEDirectMessage.builder()
                                .serialNumber(serial)
                                .productType(vp.type())
                                .build();

                        victronDevices.add(vp);

                        assertEquals(serial, vp.serialNumber());
                        VictronProduct.byProductId(vp.type().getProductId())
                                .ifPresentOrElse(type -> assertEquals(type, vp.type()), () ->
                                        fail("Must be able to find VictronProductType by productId."));

                        VictronProduct.byProductId(vp.type().getProductIdHex())
                                .ifPresentOrElse(type -> assertEquals(type, vp.type()), () ->
                                        fail("Must be able to find VictronProductType by hexProductId: "
                                        + vp.type().getProductIdHex()
                                        ));

                        VictronDevice.of(veDirectMessage)
                                .ifPresentOrElse(vp2 -> assertEquals(vp, vp2), () ->
                                        fail("Must be able to ascertain VictronProduct by a Message."));

                        victronDevices.add(vp);
                    });
                });
        // Two UUIDs.
        assertEquals(VictronProduct.values().length*2, victronDevices.size());
    }
}
