package io.freedriver.serial.api.connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class SerialDeviceIdentityTest {
    @Test
    void parsesUsbSymlink() {
        SerialDeviceIdentity identity = SerialDeviceIdentity.parse(
                Paths.get("/dev/serial/by-id/usb-1a86_7523_USB_Serial-if00-port0"),
                "usb-1a86_7523_USB_Serial-if00-port0");

        assertTrue(identity.bus().isPresent());
        assertEquals("usb", identity.bus().get());
        assertEquals("1a86", identity.vendor().orElseThrow());
        assertEquals("7523", identity.product().orElseThrow());
        assertEquals("USB_Serial", identity.serial().orElseThrow());
    }

    @Test
    void parsesVictronSymlink() {
        SerialDeviceIdentity identity = SerialDeviceIdentity.parse(
                Paths.get("/dev/serial/by-id/usb-VictronEnergy_BV_VE_Direct_cable_VE6HQ20-if00-port0"),
                "usb-VictronEnergy_BV_VE_Direct_cable_VE6HQ20-if00-port0");

        assertEquals("VictronEnergy", identity.vendor().orElseThrow());
        assertEquals("BV", identity.product().orElseThrow());
        assertEquals("VE_Direct_cable_VE6HQ20", identity.serial().orElseThrow());
        assertTrue(identity.matchesLinkNamePrefix("usb-VictronEnergy_BV_VE_Direct_cable"));
    }
}