package io.freedriver.daly.bms.exception;

import java.nio.ByteBuffer;

public class BmsException extends RuntimeException {
    public BmsException(String message) {
        super(message);
    }

    public BmsException(String message, Throwable cause) {
        super(message, cause);
    }

    public static String encodeHex(byte b) {
        return Integer.toHexString(ByteBuffer.wrap(new byte[] {0,0,0,b}).getInt());
    }
}
