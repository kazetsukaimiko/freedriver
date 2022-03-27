package io.freedriver.serial.api;

import org.junit.jupiter.api.RepeatedTest;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ByteConverterTest {
    private static final int REPETITIONS = 100;
    private static final Random RANDOM = new Random();

    @RepeatedTest(REPETITIONS)
    public void testIntegerConversion() {
        int test = RANDOM.nextInt();
        byte[] converted = ByteConverter.intToByteArray(test);
        assertEquals(Integer.BYTES, converted.length);
        int result = ByteConverter.byteArrayToInt(converted);
        assertEquals(test, result);
    }

    @RepeatedTest(REPETITIONS)
    public void testCharConversion() {
        char test = (char) RANDOM.nextInt();
        byte[] converted = ByteConverter.charToByteArray(test);
        assertEquals(Character.BYTES, converted.length);
        char result = ByteConverter.byteArrayToChar(converted);
        assertEquals(test, result);
    }

    @RepeatedTest(REPETITIONS)
    public void testIntToByteToInt() {
        int test = RANDOM.nextInt(255);
        byte converted = ByteConverter.intToSingleByte(test);
        int result = ByteConverter.singleByteToInt(converted);
        assertEquals(test, result);
    }

    @RepeatedTest(REPETITIONS)
    public void testByteToIntToByte() {
        byte[] array = new byte[RANDOM.nextInt(4096) + 4096];
        RANDOM.nextBytes(array);
        for (byte test : array) {
            int converted = ByteConverter.singleByteToInt(test);
            byte result = ByteConverter.intToSingleByte(converted);
            assertEquals(test, result);
        }
    }

    @RepeatedTest(REPETITIONS)
    public void testTooLargeNumbers() {
        // We can't convert int > 256 into a single byte
        int test = RANDOM.nextInt() + 256;
        assertThrows(IndexOutOfBoundsException.class, () -> ByteConverter.intToSingleByte(test));
    }

    @RepeatedTest(REPETITIONS)
    public void testTooLargeChars() {
        // We can't convert chars > 255 to single byte
        int sourceInt = RANDOM.nextInt(255) + 256;
        char test = (char) sourceInt;
        assertThrows(IndexOutOfBoundsException.class, () -> ByteConverter.charToSingleByte(test));
    }

    @RepeatedTest(REPETITIONS)
    public void testCharToByteToChar() {
        char test = (char) RANDOM.nextInt(255);
        byte b = ByteConverter.charToSingleByte(test);
        char result = ByteConverter.singleByteToChar(b);
        assertEquals(test, result);
    }

    @RepeatedTest(REPETITIONS)
    public void testByteToCharToByte() {
        byte[] array = new byte[1];
        RANDOM.nextBytes(array);
        byte test = array[0];
        char c = ByteConverter.singleByteToChar(test);
        byte result = ByteConverter.charToSingleByte(c);
        assertEquals(test, result);
    }
}
