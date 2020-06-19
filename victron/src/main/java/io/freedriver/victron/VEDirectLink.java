package io.freedriver.victron;

import io.freedriver.serial.SerialResource;

public class VEDirectLink {
    private final VEDirectReader device;
    private final SerialResource serial;

    public VEDirectLink(VEDirectReader device, SerialResource serial) {
        this.device = device;
        this.serial = serial;
    }


}
