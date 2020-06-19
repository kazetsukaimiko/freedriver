package io.freedriver.victron.hex;

public enum HexCommand {
    PING(0x01),
    APP_VERSION(0x03),
    PRODUCT_ID(0x04),
    RESTART(0x06),
    GET(0x07),
    SET(0x08),
    ASYNC(0x0A)
    ;

    private final byte value;

    HexCommand(int value) {
        this(Integer.valueOf(value).byteValue());
    }

    HexCommand(byte value) {
        this.value = value;
    }


    public byte calculateCheck(byte[] data) {
        byte sum = 0x00;
        for (int i=0; i<data.length; i++) {
            sum = (byte) ((int) sum + (int) data[i]);
        }
        return sum; // TODO: 0x55
    }

    public byte[] format(byte[] data) {
        return String.valueOf(":" + String.valueOf(data) + calculateCheck(data))
                .getBytes();
    }
}
