package io.freedriver.daly.bms;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Response extends Signal {

    private final Address address;
    private final QueryId queryId;
    private final byte checksum;
    private final int dataLength;
    private final byte[] data;

    @Builder(toBuilder = true)
    public Response(Address address, QueryId queryId, byte checksum, int dataLength, byte[] data) {
        this.address = address;
        this.queryId = queryId;
        this.checksum = (byte) (checksum & 0xFF);
        this.dataLength = dataLength;
        this.data = data == null ? new byte[0] : data;
    }

}
