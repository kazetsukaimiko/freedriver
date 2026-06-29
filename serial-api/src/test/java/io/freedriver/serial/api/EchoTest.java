package io.freedriver.serial.api;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.Test;

public abstract class EchoTest<SR extends SerialResource> extends SerialApiTest<SR> {


    public static final Random RANDOM = new Random();

    @Test
    public void test() {
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
