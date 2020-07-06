package io.freedriver.serial.params;

public enum DataBits implements DataBit {
    DATABITS_5(5),
    DATABITS_6(6),
    DATABITS_7(7),
    DATABITS_8(8);

    private final int dataBits;

    DataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    public int getDataBits() {
        return dataBits;
    }
}
