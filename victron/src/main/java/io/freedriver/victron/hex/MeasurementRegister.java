package io.freedriver.victron.hex;

import io.freedriver.victron.VEDirectColumn;

public enum MeasurementRegister implements Register {
    MAIN_VOLTAGE(VEDirectColumn.MAIN_VOLTAGE, (byte) 0b00000001); // TODO real register

    private final VEDirectColumn column;
    private final byte id;

    MeasurementRegister(VEDirectColumn column, byte id) {
        this.column = column;
        this.id = (byte) id;
    }

    @Override
    public byte id() {
        return 0;
    }



}
