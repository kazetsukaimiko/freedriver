package io.freedriver.victron.jpa;

import io.freedriver.victron.FirmwareVersion;
import org.junit.jupiter.api.Test;

public class FirmwareVersionConverterTest {

    FirmwareVersionConverter victim = new FirmwareVersionConverter();

    @Test
    public void testVOneFiveO() {
        FirmwareVersion firmwareVersion = victim.convertToEntityAttribute("v1.50");
    }
}
