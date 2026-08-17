package io.freedriver.daly.bms;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Request extends Signal  {
    private final Address address;
    private final QueryId queryId;
    private final byte checksum;
    private final DalyCommand command;
    private final byte[] data;
    private final int dataLength;

    public Request(DalyCommand command, byte[] data) {
        this(null, null, (byte) 0, command, data);
    }

    @Builder(toBuilder = true)
    public Request(Address address, QueryId queryId, byte checksum, DalyCommand command, byte[] data) {
        this.address = address;
        this.queryId = queryId;
        this.checksum = (byte) (checksum & 0xFF);
        this.command = command;
        this.data = data;
        this.dataLength = data == null ? 0 : data.length;
    }

    /*
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

     */

    /*
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

     */

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
