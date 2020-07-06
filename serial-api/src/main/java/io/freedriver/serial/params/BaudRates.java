package io.freedriver.serial.params;

public enum BaudRates implements BaudRate {
    BAUDRATE_110(110),
    BAUDRATE_300(300),
    BAUDRATE_600(600),
    BAUDRATE_1200(1200),
    BAUDRATE_2400(2400),
    BAUDRATE_4800(4800),
    BAUDRATE_9600(9600),
    BAUDRATE_14400(14400),
    BAUDRATE_19200(19200),
    BAUDRATE_38400(38400),
    BAUDRATE_57600(57600),
    BAUDRATE_115200(115200),
    BAUDRATE_128000(128000),
    BAUDRATE_256000(256000);

    private final int baudRate;

    BaudRates(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getBaudRate() {
        return baudRate;
    }
}
