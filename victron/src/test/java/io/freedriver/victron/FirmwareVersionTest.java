package io.freedriver.victron;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class FirmwareVersionTest {

    @Test
    public void testDocumentationFWUseCase() {
        String fw = "C208";
        FirmwareVersion fwv = new FirmwareVersion(fw);

        assertEquals(fw, fwv.representation());
        assertEquals("C", fwv.candidate());
        assertEquals(new BigDecimal("2.08"), fwv.version());
        assertNull(fwv.beta());
        assertFalse(fwv.release());

        assertEquals("v2.08-rc-C", fwv.toString());
    }

    @Test
    public void testReleaseFWUseCase() {
        String fw = "208";
        FirmwareVersion fwv = new FirmwareVersion(fw);
        assertEquals(fw, fwv.representation());
        assertEquals(new BigDecimal("2.08"), fwv.version());
        assertNull(fwv.beta());
        assertNull(fwv.candidate());
        assertTrue(fwv.release());

        assertEquals("v2.08", fwv.toString());
    }

    @Test
    public void testDocumentationFWEUseCase() {
        String fwe = "0208FF";
        FirmwareVersion fwev = new FirmwareVersion(fwe);

        assertEquals(fwe, fwev.representation());
        assertEquals(new BigDecimal("2.08"), fwev.version());
        assertNull(fwev.beta());
        assertNull(fwev.candidate());
        assertTrue(fwev.release());

        assertEquals("v2.08", fwev.toString());
    }

    @Test
    public void testBetaFWEUseCase() {
        String fwe = "20801";
        FirmwareVersion fwev = new FirmwareVersion(fwe);

        assertEquals(fwe, fwev.representation());
        assertEquals(new BigDecimal("2.08"), fwev.version());
        assertEquals("01", fwev.beta());
        assertNull(fwev.candidate());
        assertFalse(fwev.release());

        assertEquals("v2.08-beta-01", fwev.toString());
    }

    @Test
    public void testUnsupportedVersionException() {
        assertThrows(IllegalArgumentException.class, () -> new FirmwareVersion("abcdefhijklmnop"));
    }
}
