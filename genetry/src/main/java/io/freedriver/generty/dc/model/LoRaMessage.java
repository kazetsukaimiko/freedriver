package io.freedriver.generty.dc.model;

import java.util.Map;
import java.util.UUID;

public class LoRaMessage {
    private UUID messageId;
    private LoRaEncryptionAlgorithm encryption;
    private LoRaMessageData data;

    private Map<String, String> metadata;

    public UUID getMessageId() {
        return messageId;
    }

    public LoRaEncryptionAlgorithm getEncryption() {
        return encryption;
    }

    public LoRaMessageData getData() {
        return data;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
