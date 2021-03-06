package io.freedriver.serial.api.params;

public class SerialParams implements BaudRate, DataBit, StopBit, Parity {
    private BaudRate baudRate = BaudRates.BAUDRATE_115200;
    private DataBit dataBits = DataBits.DATABITS_8;
    private StopBit stopBits = StopBits.STOPBITS_1;
    private Parity parity = Parities.PARITY_NONE;


    /*
    SerialPort.BAUDRATE_115200,
    SerialPort.DATABITS_8,
    SerialPort.STOPBITS_1,
    SerialPort.PARITY_NONE

     */

    public SerialParams() {
    }

    public int getBaudRate() {
        return baudRate.getBaudRate();
    }

    public SerialParams setBaudRate(BaudRate baudRate) {
        this.baudRate = baudRate;
        return this;
    }

    public int getDataBits() {
        return dataBits.getDataBits();
    }

    public SerialParams setDataBits(DataBit dataBits) {
        this.dataBits = dataBits;
        return this;
    }

    public int getStopBits() {
        return stopBits.getStopBits();
    }

    public SerialParams setStopBits(StopBit stopBits) {
        this.stopBits = stopBits;
        return this;
    }

    public int getParity() {
        return parity.getParity();
    }

    public SerialParams setParity(Parity parity) {
        this.parity = parity;
        return this;
    }
}
