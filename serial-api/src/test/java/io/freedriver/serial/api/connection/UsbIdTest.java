package io.freedriver.serial.api.connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class UsbIdTest {
    @Test
    void normalizesHexAndPadding() {
        assertEquals(new UsbId("2341", "0042"), UsbId.parse("0x2341:42"));
        assertEquals("2341:0042", UsbId.parse("2341:0042").dotted());
    }

    @Test
    void arduinoMegaDefaults() {
        assertEquals(2, UsbId.arduinoMega2560().size());
        assertEquals(UsbId.ARDUINO_SA_MEGA_2560, UsbId.parse("2341:0042"));
        assertEquals(UsbId.ARDUINO_ORG_MEGA_2560, UsbId.parse("2a03:0042"));
    }

    @Test
    void rejectsBadSpec() {
        assertThrows(IllegalArgumentException.class, () -> UsbId.parse("2341"));
        assertThrows(IllegalArgumentException.class, () -> UsbId.parse("zzzz:0042"));
    }
}
