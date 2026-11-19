package io.freedriver.ee.cdi.producer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class NitriteCredentialsTest {
    @Test
    void toStringMasksPassword() {
        NitriteCredentials credentials = new NitriteCredentials("store-user", "store-secret-value");
        String text = credentials.toString();
        assertTrue(text.contains("password=***"));
        assertTrue(text.contains("store-user"));
        assertFalse(text.contains("store-secret-value"));
    }
}
