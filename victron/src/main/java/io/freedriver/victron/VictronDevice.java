package io.freedriver.victron;

import java.util.Optional;

import lombok.Builder;

@Builder(toBuilder = true)
public record VictronDevice(VictronProduct type, String serialNumber) {

    /**
     * There's no guarantee a VEDirectMessage will contain all necessary VictronProduct information.
     */
    public static Optional<VictronDevice> of(VEDirectMessage veDirectMessage) {
        return Optional.ofNullable(veDirectMessage)
                .flatMap(message -> of(veDirectMessage.productType(), veDirectMessage.serialNumber()));
    }

    public static Optional<VictronDevice> of(VictronProduct productType, String productSerialNumber) {
        if (productType != null && productSerialNumber != null) {
            return Optional.of(new VictronDevice(productType, productSerialNumber));
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "(Serial #:"+serialNumber()+") " + type().getProductName();
    }
}
