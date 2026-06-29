package io.freedriver.serial;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import io.freedriver.serial.api.SerialResource;
import org.junit.jupiter.api.Test;

// TODO : Move up to api project

public abstract class EchoTestZ<SR extends SerialResource> {

    public static final Random RANDOM = new Random();

    public abstract SR construct();

    @Test
    public void testDefault() {
        testResource(construct());
    }

    public void testResource(SR resource) {
        int size = RANDOM.nextInt(1024) + 16;

        byte[] written = new byte[size];
        for (int idx = 0; idx < size; idx++) {
            written[idx] = (byte) RANDOM.nextInt();
        }

        resource.write(written);
        byte[] read = new byte[size];
        for (int idx = 0; idx < size; idx++) {
            read[idx] = resource.read(1)[0];
        }

        for (int idx = 0; idx < size; idx++) {
            assertEquals(written[idx], read[idx]);
        }
    }
}
