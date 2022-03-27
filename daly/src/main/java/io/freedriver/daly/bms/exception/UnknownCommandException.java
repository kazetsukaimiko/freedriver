package io.freedriver.daly.bms.exception;

public class UnknownCommandException extends BmsException {
    public UnknownCommandException(byte b) {
        super(formatMessage(b));
    }

    public static String formatMessage(byte b) {
        return "Unknown command flag 0x" + encodeHex(b);
    }
}
