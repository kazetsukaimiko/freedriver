package io.freedriver.victron;

import java.util.Objects;
import java.util.Optional;

public class VictronDevice {
    private VictronProduct type;
    private String serialNumber;

    public VictronDevice() {
        // Jackson
    }

    public VictronDevice(VictronProduct type, String serialNumber) {
        this.type = type;
        this.serialNumber = serialNumber;
    }

    /**
     * There's no guarantee a VEDirectMessage will contain all necessary VictronProduct information.
     * @param veDirectMessage
     * @return
     */
    public static Optional<VictronDevice> of(VEDirectMessage veDirectMessage) {
        return Optional.ofNullable(veDirectMessage)
                .flatMap(message -> of(veDirectMessage.getProductType(), veDirectMessage.getSerialNumber()));
    }

    public static Optional<VictronDevice> of(VictronProduct productType, String productSerialNumber) {
        if (productType != null && productSerialNumber != null) {
            return Optional.of(new VictronDevice(productType, productSerialNumber));
        }
        return Optional.empty();
    }

    public VictronProduct getType() {
        return type;
    }

    public void setType(VictronProduct type) {
        this.type = type;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VictronDevice that = (VictronDevice) o;
        return type == that.type &&
                Objects.equals(serialNumber, that.serialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, serialNumber);
    }

    @Override
    public String toString() {
        return "(Serial #:"+getSerialNumber()+") " + getType().getProductName();
    }
}
