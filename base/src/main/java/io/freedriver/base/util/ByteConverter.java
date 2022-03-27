package io.freedriver.base.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public interface ByteConverter {
    static int byteArrayToInt(byte[] b) {
        return ByteBuffer.wrap(b).getInt();
    }

    static byte[] intToByteArray(int i) {
        return ByteBuffer.allocate(Integer.BYTES).putInt(i).array();
    }

    static byte intToSingleByte(int i) {
        byte[] array = intToByteArray(i);
        if (array[0] != 0x00 || array[1] != 0x00 || array[2] != 0x00) {
            throw new IndexOutOfBoundsException(String.format("Unable to convert %d to single byte: Too large", i));
        }
        return array[3];
    }

    static int singleByteToInt(byte b) {
        return byteArrayToInt(new byte[] {0x00, 0x00, 0x00, b});
    }

    static char byteArrayToChar(byte[] b) {
        return ByteBuffer.wrap(b).getChar();
    }

    static byte[] charToByteArray(char c) {
        return ByteBuffer.allocate(Character.BYTES).putChar(c).array();
    }

    static char singleByteToChar(byte b) {
        return byteArrayToChar(new byte[] {0x00, b});
    }

    static byte charToSingleByte(char c) {
        byte[] array = charToByteArray(c);
        if (array[0] != 0x00) {
            throw new IndexOutOfBoundsException(String.format("Unable to convert %c to single byte: Too large", c));
        }
        return array[1];
    }

    static String byteArrayToString(byte[] array) {
        return new String(array, StandardCharsets.UTF_8);
    }

    static byte[] stringToByteArray(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }
}
