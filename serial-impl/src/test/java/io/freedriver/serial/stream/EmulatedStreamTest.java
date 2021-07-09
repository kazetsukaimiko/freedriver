package io.freedriver.serial.stream;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmulatedStreamTest {

    @Test
    public void testEmulator() throws IOException {
        SerialStreamEmulator emulator = new SerialStreamEmulator();
        String randomString = TestBean.randomString();

        byte[] bytesWritten = randomString.getBytes(StandardCharsets.UTF_8);
        emulator.getOutputStream().write(bytesWritten);

        String readOut = new String(emulator.getInputStream().readNBytes(bytesWritten.length), StandardCharsets.UTF_8);
        assertEquals(randomString, readOut);
    }
}
