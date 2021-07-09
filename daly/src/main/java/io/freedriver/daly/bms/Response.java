package io.freedriver.daly.bms;

import io.freedriver.daly.bms.checksum.CRC8;

public class Response extends Signal {

    private int dataLength = 8;
    private byte[] data = new byte[8];

    public Response() {
    }

    @Override
    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}
