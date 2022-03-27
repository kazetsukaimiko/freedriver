package io.freedriver.serial.stream.api;

import io.freedriver.serial.api.SerialApiTest;
import io.freedriver.serial.api.SerialResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class StreamsEchoTest<SR extends SerialResource> extends SerialApiTest<SR> {
    SerialInputStream inputStream;
    SerialOutputStream outputStream;

    @BeforeEach
    public void init() {
        SR resource = construct();
        outputStream = new SerialOutputStream(resource);
        inputStream = new SerialInputStream(resource);
    }

    @Test
    public void testEcho() throws IOException {
        int sampleSize = (RANDOM.nextInt(511) + 1) * 4096;
        for (int i = 0; i < sampleSize; i++) {
            int test = RANDOM.nextInt(255);
            outputStream.write(test);
            int result = inputStream.read();
            assertEquals(test, result);
        }
    }

    @Test
    public void testByteEcho() throws IOException {
        List<byte[]> arraysWritten = new ArrayList<>();
        int sampleSize = 40; //(RANDOM.nextInt(511) + 1) * 4096;
        for (int i = 0; i < sampleSize; i++) {
            // Make an toWriteArray of a random size.
            byte[] toWriteArray = new byte[] { 0x01, 0x02, 0x03, 0x04 }; // new byte[RANDOM.nextInt(16) * Integer.BYTES];
            // Randomize the toWriteArray
            RANDOM.nextBytes(toWriteArray);
            // Write the toWriteArray.
            arraysWritten.add(toWriteArray);
            outputStream.write(toWriteArray);
            outputStream.flush();
            // Try to read an equivalent toWriteArray.
            byte[] fromInput = new byte[toWriteArray.length];
            assertEquals(toWriteArray.length, fromInput.length);
            int lengthRead = inputStream.read(fromInput);
            for (int idx = 0; idx < fromInput.length; idx++) {
                assertEquals(toWriteArray[idx], fromInput[idx]);
            }
        }
    }
}
