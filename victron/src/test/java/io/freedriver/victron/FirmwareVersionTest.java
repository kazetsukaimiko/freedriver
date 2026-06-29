package io.freedriver.victron;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class FirmwareVersionTest {

    @Test
    public void testDocumentationFWUseCase() {
        String fw = "C208";
        FirmwareVersion fwv = new FirmwareVersion(fw);

        assertEquals(fw, fwv.getRepresentation());
        assertEquals("C", fwv.getCandidate());
        assertEquals(new BigDecimal("2.08"), fwv.getVersion());
        assertNull(fwv.getBeta());
        assertFalse(fwv.isRelease());

        assertEquals("v2.08-rc-C", fwv.toString());
    }

    @Test
    public void testReleaseFWUseCase() {
        String fw = "208";
        FirmwareVersion fwv = new FirmwareVersion(fw);
        assertEquals(fw, fwv.getRepresentation());
        assertEquals(new BigDecimal("2.08"), fwv.getVersion());
        assertNull(fwv.getBeta());
        assertNull(fwv.getCandidate());
        assertTrue(fwv.isRelease());

        assertEquals("v2.08", fwv.toString());
    }

    @Test
    public void testDocumentationFWEUseCase() {
        String fwe = "0208FF";
        FirmwareVersion fwev = new FirmwareVersion(fwe);

        assertEquals(fwe, fwev.getRepresentation());
        assertEquals(new BigDecimal("2.08"), fwev.getVersion());
        assertNull(fwev.getBeta());
        assertNull(fwev.getCandidate());
        assertTrue(fwev.isRelease());

        assertEquals("v2.08", fwev.toString());
    }

    @Test
    public void testBetaFWEUseCase() {
        String fwe = "20801";
        FirmwareVersion fwev = new FirmwareVersion(fwe);

        assertEquals(fwe, fwev.getRepresentation());
        assertEquals(new BigDecimal("2.08"), fwev.getVersion());
        assertEquals("01", fwev.getBeta());
        assertNull(fwev.getCandidate());
        assertFalse(fwev.isRelease());

        assertEquals("v2.08-beta-01", fwev.toString());
    }

    @Test
    public void testUnsupportedVersionException() {
        assertThrows(IllegalArgumentException.class, () -> new FirmwareVersion("abcdefhijklmnop"));
    }
}
