package io.freedriver.serial.params;

public enum StopBits implements StopBit {
    STOPBITS_1(1),
    STOPBITS_2(2),
    STOPBITS_1_5(3);

    private final int stopBits;

    StopBits(int stopBits) {
        this.stopBits = stopBits;
    }

    public int getStopBits() {
        return stopBits;
    }
}
