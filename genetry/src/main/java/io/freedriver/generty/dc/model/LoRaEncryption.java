package io.freedriver.generty.dc.model;

public class LoRaEncryption {
    private String keyId;
    private LoRaEncryptionAlgorithm type;

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public LoRaEncryptionAlgorithm getType() {
        return type;
    }

    public void setType(LoRaEncryptionAlgorithm type) {
        this.type = type;
    }
}
