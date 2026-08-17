package io.freedriver.serial.api.params;

import lombok.Builder;

@Builder(toBuilder = true)
public record SerialParams(BaudRate baudRate, DataBit dataBits, StopBit stopBits, Parity parity)
        implements BaudRate, DataBit, StopBit, Parity {

    public SerialParams {
        if (baudRate == null) {
            baudRate = BaudRates.BAUDRATE_115200;
        }
        if (dataBits == null) {
            dataBits = DataBits.DATABITS_8;
        }
        if (stopBits == null) {
            stopBits = StopBits.STOPBITS_1;
        }
        if (parity == null) {
            parity = Parities.PARITY_NONE;
        }
    }

    public SerialParams() {
        this(null, null, null, null);
    }

    @Override
    public int getBaudRate() {
        return baudRate.getBaudRate();
    }

    public SerialParams setBaudRate(BaudRate baudRate) {
        return toBuilder().baudRate(baudRate).build();
    }

    @Override
    public int getDataBits() {
        return dataBits.getDataBits();
    }

    public SerialParams setDataBits(DataBit dataBits) {
        return toBuilder().dataBits(dataBits).build();
    }

    @Override
    public int getStopBits() {
        return stopBits.getStopBits();
    }

    public SerialParams setStopBits(StopBit stopBits) {
        return toBuilder().stopBits(stopBits).build();
    }

    @Override
    public int getParity() {
        return parity.getParity();
    }

    public SerialParams setParity(Parity parity) {
        return toBuilder().parity(parity).build();
    }
}
