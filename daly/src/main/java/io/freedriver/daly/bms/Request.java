package io.freedriver.daly.bms;

import io.freedriver.daly.bms.checksum.CRC8;

public class Request extends Signal {
    // Example
    public static final byte[] EXAMPLE = new byte[] {
            // Flag
            (byte) 0xa5,
            // Address
            (byte) 0x40,
            // CommandId
            (byte) 0x95,
            // Data length, bytes
            (byte) 0x8,
            // Payload, all zeroes
            (byte) 0x0,
            (byte) 0x0,
            (byte) 0x0,
            (byte) 0x0,
            (byte) 0x0,
            (byte) 0x0,
            (byte) 0x0,
            (byte) 0x0,
            // Checksum
            (byte) 0x82,
            // NEWLINE
            (byte) 0xa
    };

    private final int dataLength = 8;
    private final byte[] data = new byte[8];

    public Request() {
    }

    public Request(CommandId commandId) {
        setCommandId(commandId);
    }

    @Override
    public int getDataLength() {
        return dataLength;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public byte[] toFullMessage() {
        byte[] withoutCrc = super.toFullMessage();
        withoutCrc[withoutCrc.length-1] = CRC8.calc(withoutCrc, withoutCrc.length-1);
        return withoutCrc;
    }
}
