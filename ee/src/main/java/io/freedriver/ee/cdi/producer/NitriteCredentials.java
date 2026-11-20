package io.freedriver.ee.cdi.producer;

/**
 * User id and password for one Nitrite store, read from {@code nitrite-credentials.json}.
 */
public record NitriteCredentials(String userId, String password) {
    @Override
    public String toString() {
        return "NitriteCredentials[userId=" + userId + ", password=***]";
    }
}
