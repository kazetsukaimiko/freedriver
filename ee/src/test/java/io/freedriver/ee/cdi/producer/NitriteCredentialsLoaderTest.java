package io.freedriver.ee.cdi.producer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Optional;
import java.util.Set;

import io.freedriver.ee.convention.AppData;
import io.freedriver.ee.testing.EeLogCapture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NitriteCredentialsLoaderTest {
    private static final String USER = "xq-user-7c1e";
    private static final String PASSWORD = "xq-pass-7c1e";

    @TempDir
    Path temp;

    @Test
    void readsUserIdAndPasswordFromTheCredentialsFile() throws IOException {
        writeCredentials(temp, USER, PASSWORD);
        NitriteCredentials credentials = NitriteProducer.loadCredentials(temp);
        assertEquals(USER, credentials.userId());
        assertEquals(PASSWORD, credentials.password());
    }

    @Test
    void missingFileNamesTheFileAndCreatesNothing() {
        Path deployment = temp.resolve("absent-deployment");
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> NitriteProducer.loadCredentials(deployment));
        assertTrue(error.getMessage().contains(NitriteProducer.CREDENTIALS_FILE));
        assertFalse(error.getMessage().contains(USER));
        assertFalse(error.getMessage().contains(PASSWORD));
        assertFalse(Files.exists(deployment));
    }

    @Test
    void unreadableFileNamesTheFile() throws IOException {
        Path file = temp.resolve(NitriteProducer.CREDENTIALS_FILE);
        Files.writeString(file, credentialsJson(USER, PASSWORD));
        Files.setPosixFilePermissions(file, Set.of());
        try {
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                    () -> NitriteProducer.loadCredentials(temp));
            assertTrue(error.getMessage().contains(NitriteProducer.CREDENTIALS_FILE));
            assertTrue(error.getMessage().contains("not readable"));
            assertFalse(error.getMessage().contains(USER));
            assertFalse(error.getMessage().contains(PASSWORD));
        } finally {
            Files.setPosixFilePermissions(file, Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE));
        }
    }

    @Test
    void malformedFileNamesTheFileAndLeaksNothing() throws IOException {
        Path file = temp.resolve(NitriteProducer.CREDENTIALS_FILE);
        Files.writeString(file, "{ \"userId\": \"" + USER + "\", \"password\": \"" + PASSWORD + "\"");
        try (EeLogCapture logs = new EeLogCapture()) {
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                    () -> NitriteProducer.loadCredentials(temp));
            assertTrue(error.getMessage().contains(NitriteProducer.CREDENTIALS_FILE));
            assertFalse(error.getMessage().contains(USER));
            assertFalse(error.getMessage().contains(PASSWORD));
            logs.assertAbsent(USER);
            logs.assertAbsent(PASSWORD);
        }
    }

    @Test
    void nullRootNamesTheFile() throws IOException {
        Files.writeString(temp.resolve(NitriteProducer.CREDENTIALS_FILE), "null");
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> NitriteProducer.loadCredentials(temp));
        assertTrue(error.getMessage().contains(NitriteProducer.CREDENTIALS_FILE));
        assertTrue(error.getMessage().contains("null root"));
    }

    @Test
    void loadAsMapsANullDocumentToEmpty() throws IOException {
        Path file = temp.resolve("null-root.json");
        Files.writeString(file, "null");
        Optional<Object> loaded = AppData.loadAs(Object.class, file);
        assertTrue(loaded.isEmpty());
    }

    @Test
    void wrongFieldTypesNameTheFieldAndOmitTheValue() throws IOException {
        assertWrongType("{\"userId\":\"" + USER + "\",\"password\":123}", "password", "123");
        assertWrongType("{\"userId\":\"" + USER + "\",\"password\":{\"raw\":\"" + PASSWORD + "\"}}", "password", PASSWORD);
        assertWrongType("{\"userId\":\"" + USER + "\",\"password\":[\"" + PASSWORD + "\"]}", "password", PASSWORD);
        assertWrongType("{\"userId\":\"" + USER + "\",\"password\":true}", "password", "true");
        assertWrongType("{\"userId\":\"" + USER + "\",\"password\":null}", "password", "null");
        assertWrongType("{\"userId\":123,\"password\":\"" + PASSWORD + "\"}", "userId", "123");
    }

    @Test
    void blankOrMissingFieldsNameTheField() throws IOException {
        assertBlank("{\"userId\":\"\",\"password\":\"" + PASSWORD + "\"}", "userId");
        assertBlank("{\"userId\":\"   \",\"password\":\"" + PASSWORD + "\"}", "userId");
        assertBlank("{\"userId\":\"" + USER + "\",\"password\":\"\"}", "password");
        assertBlank("{\"password\":\"" + PASSWORD + "\"}", "userId");
        assertBlank("{\"userId\":\"" + USER + "\"}", "password");
    }

    private void assertWrongType(String json, String field, String leaked) throws IOException {
        Files.writeString(temp.resolve(NitriteProducer.CREDENTIALS_FILE), json);
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> NitriteProducer.loadCredentials(temp));
        assertTrue(error.getMessage().contains(field));
        assertTrue(error.getMessage().contains("not a string"));
        assertFalse(error.getMessage().contains(leaked));
        assertFalse(error.getMessage().contains(USER));
        assertFalse(error.getMessage().contains(PASSWORD));
    }

    private void assertBlank(String json, String field) throws IOException {
        Files.writeString(temp.resolve(NitriteProducer.CREDENTIALS_FILE), json);
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> NitriteProducer.loadCredentials(temp));
        assertTrue(error.getMessage().contains(field));
        assertTrue(error.getMessage().contains("missing or blank"));
        assertFalse(error.getMessage().contains(USER));
        assertFalse(error.getMessage().contains(PASSWORD));
    }

    private static void writeCredentials(Path directory, String userId, String password) throws IOException {
        Files.writeString(directory.resolve(NitriteProducer.CREDENTIALS_FILE), credentialsJson(userId, password));
    }

    private static String credentialsJson(String userId, String password) {
        return "{\"userId\":\"" + userId + "\",\"password\":\"" + password + "\"}";
    }
}
