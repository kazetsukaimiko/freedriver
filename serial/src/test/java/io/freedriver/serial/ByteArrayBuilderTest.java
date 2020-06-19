package io.freedriver.serial;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteArrayBuilderTest {

    @Test
    public void testResultingByteArrayLength() {
        ByteArrayBuilder bab = new ByteArrayBuilder();
        byte[] zeroLength = bab.build();

        assertEquals(0, zeroLength.length, "Build out of the gate should result in a zero length array.");
        assertArrayEquals(new byte[] {}, zeroLength, "Zero length array");

        bab.append((byte) 7, (byte) 6, (byte) 5);
        byte[] threeLength = bab.build();

        assertEquals(3, threeLength.length, "Build should result in a 3 length array.");
        assertArrayEquals(new byte[]{ 7, 6, 5 }, threeLength, "Three length array");

        bab.append((byte) 1);
        bab.append((byte) 2);
        byte[] fiveLength = bab.build();

        assertEquals(5, fiveLength.length, "Build should result in a 3 length array.");
        assertArrayEquals(new byte[]{ 7, 6, 5, 1, 2 }, fiveLength, "Five length array");
    }
}
