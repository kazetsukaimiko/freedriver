package io.freedriver.victron;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class VictronDeviceTest {
    private static final Logger LOGGER = Logger.getLogger(VictronDeviceTest.class.getName());

    @Test
    public void testVictronProduct() {
        Set<VictronDevice> victronDevices = new HashSet<>();

        Stream.of(UUID.randomUUID().toString(), UUID.randomUUID().toString())
                .forEach(serial -> {
            Stream.of(VictronProduct.values())
                    .map(type -> {
                        VictronDevice vp = new VictronDevice();
                        vp.setSerialNumber(serial);
                        vp.setType(type);
                        return vp;
                    })
                    .peek(vp -> LOGGER.info(vp.toString()))
                    .forEach(vp -> {
                        VEDirectMessage veDirectMessage = new VEDirectMessage();
                        veDirectMessage.setSerialNumber(serial);
                        veDirectMessage.setProductType(vp.getType());

                        victronDevices.add(vp);

                        assertEquals(serial, vp.getSerialNumber());
                        VictronProduct.byProductId(vp.getType().getProductId())
                                .ifPresentOrElse(type -> assertEquals(type, vp.getType()), () ->
                                        fail("Must be able to find VictronProductType by productId."));

                        VictronProduct.byProductId(vp.getType().getProductIdHex())
                                .ifPresentOrElse(type -> assertEquals(type, vp.getType()), () ->
                                        fail("Must be able to find VictronProductType by hexProductId: "
                                        + vp.getType().getProductIdHex()
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
