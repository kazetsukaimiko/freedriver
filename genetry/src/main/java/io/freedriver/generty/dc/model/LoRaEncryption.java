package io.freedriver.generty.dc.model;

import lombok.Builder;

@Builder(toBuilder = true)
public record LoRaEncryption(String keyId, LoRaEncryptionAlgorithm type) {
}
