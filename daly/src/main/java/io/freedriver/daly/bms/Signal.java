package io.freedriver.daly.bms;

import io.freedriver.daly.bms.checksum.CRC8;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class Signal {
    private Address address;
    private QueryId queryId;
    private byte checksum;

    public Signal() {
    }

    public abstract int getDataLength();
    public abstract byte[] getData();

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public QueryId getQueryId() {
        return queryId;
    }

    public void setQueryId(QueryId queryId) {
        this.queryId = queryId;
    }

    public byte getChecksum() {
        return (byte) (checksum & 0xFF);
    }

    public void setChecksum(byte checksum) {
        this.checksum = checksum;
    }

    public static int dalyChecksum(byte[] command) {
        return CRC8.calc(command, command.length);
    }


/*
    public byte[] toFullMessage() {
        byte[] fullMessage = new byte[4 + getDataLength() + 1];
        fullMessage[1] = getAddress().getValue();
        fullMessage[2] = getCommandId().getValue();
        fullMessage[3] = (byte) (getDataLength() & 0xF);
        for (int idx = 0; idx < getData().length; idx++) {
            fullMessage[4+idx] = getData()[idx];
        }
        fullMessage[4 + getDataLength()] = getChecksum();
        return fullMessage;
    }



 */



    public static byte[] toBytes(List<byte[]> byteableList) {
        return toBytes(byteableList.toArray(new byte[][] {}));
    }

    public static byte[] toBytes(byte[]... parts) {
        byte[] result = new byte[Stream.of(parts).mapToInt(a -> a.length).sum()];
        int idx = 0;
        for (byte[] part : parts) {
            for (byte b : part) {
                result[idx++] = b;
            }
        }
        return result;
    }


    public boolean validates() {
        byte[] fullMessage = toBytes();
        return getChecksum() == CRC8.calc(fullMessage, fullMessage.length-1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Signal signal = (Signal) o;
        return address == signal.address && queryId == signal.queryId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, queryId);
    }
}
