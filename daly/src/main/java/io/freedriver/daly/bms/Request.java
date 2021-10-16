package io.freedriver.daly.bms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Request extends Signal  {
    private final DalyCommand command;
    private final byte[] data;
    private final int dataLength;

    public Request(DalyCommand command, byte[] data) {
        this.command = command;
        this.data = data;
        this.dataLength = data.length;
    }

    public DalyCommand getCommand() {
        return command;
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
    public byte[] asByteArray() {

        byte[] checksumTarget = toBytesFromByteable(Arrays.asList(
                command,
                getAddress(),
                getQueryId(),
                () -> new byte[] { (byte) dataLength },
                () -> data
        ));

        return toBytesFromByteable(Arrays.asList(
                Flag.START,
                () -> checksumTarget,
                () -> new byte[] { (byte) dalyChecksum(checksumTarget) },
                Flag.END
        ));
    }

    @Override
    public String toString() {
        List<String> parts = new ArrayList<>();
        for (byte aByte : asByteArray()) {
            //parts.add(String.format("%02x", aByte));
            // upper case
            parts.add(String.format("%02X", aByte));
        }
        return String.join(" ", parts);
    }

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

}
