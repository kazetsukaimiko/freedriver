package io.freedriver.daly.bms;

public class Response extends Signal {

    private int dataLength = 0;
    private byte[] data = new byte[0];

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

    @Override
    public byte[] asByteArray() {
        return new byte[0];
    }
}
