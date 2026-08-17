package io.freedriver.generty.dc.model;

import java.util.Map;
import java.util.UUID;

import lombok.Builder;

@Builder(toBuilder = true)
public record LoRaMessage(
        UUID messageId,
        LoRaEncryptionAlgorithm encryption,
        LoRaMessageData data,
        Map<String, String> metadata) {
}
