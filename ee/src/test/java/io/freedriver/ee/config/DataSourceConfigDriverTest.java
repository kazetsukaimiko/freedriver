package io.freedriver.ee.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.ServiceLoader;

import io.freedriver.ee.testing.EeLogCapture;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataSourceConfigDriverTest {
    private static final String URL = "jdbc:freedriver-test:db";
    private static final String USER = "db-user-4aa1";
    private static final String PASSWORD = "db-pass-4aa1";
    private static final String SENTINEL = "io.freedriver.ee.config.sentinel.SentinelDriver";

    private static Driver registered;

    @BeforeAll
    static void registerServiceDriver() throws SQLException {
        for (Driver driver : ServiceLoader.load(Driver.class)) {
            if (driver instanceof RecordingDriver recording) {
                DriverManager.registerDriver(recording);
                registered = recording;
            }
        }
        if (registered == null) {
            throw new IllegalStateException("service loader did not provide RecordingDriver");
        }
    }

    @AfterAll
    static void deregisterServiceDriver() throws SQLException {
        if (registered != null) {
            DriverManager.deregisterDriver(registered);
        }
    }

    @BeforeEach
    void clearCalls() {
        RecordingDriver.CALLS.clear();
    }

    @Test
    void connectionUsesTheServiceDriverAndIgnoresDriverClassName() throws Exception {
        assertNull(findLoaded(SENTINEL));
        DataSourceConfig config = DataSourceConfig.builder()
                .urlString(URL)
                .username(USER)
                .password(PASSWORD)
                .driverClassName(SENTINEL)
                .driverProperties(Map.of("user", "from-properties", "password", "from-properties", "ssl", "true"))
                .build();
        DataSourceConfig rewritten = config.toBuilder().username(USER).build();
        assertEquals(USER, rewritten.username());
        try (EeLogCapture logs = new EeLogCapture();
                Connection connection = rewritten.getDatasource().getConnection()) {
            assertTrue(connection != null);
            logs.assertContains("jakarta.persistence.jdbc.driver is ignored");
            logs.assertAbsent(USER);
            logs.assertAbsent(PASSWORD);
            logs.assertAbsent(SENTINEL);
        }
        assertNull(findLoaded(SENTINEL));
        assertEquals(1, RecordingDriver.CALLS.size());
        RecordingDriver.Call call = RecordingDriver.CALLS.get(0);
        assertEquals(URL, call.url());
        assertFalse(call.url().contains(USER));
        assertFalse(call.url().contains(PASSWORD));
        assertEquals(USER, call.properties().getProperty("user"));
        assertEquals(PASSWORD, call.properties().getProperty("password"));
        assertEquals("true", call.properties().getProperty("ssl"));
    }

    @Test
    void omittedPasswordConnectsWithoutThatKey() throws Exception {
        DataSourceConfig config = DataSourceConfig.builder()
                .urlString(URL)
                .username(USER)
                .build();
        try (Connection connection = config.getDatasource().getConnection()) {
            assertTrue(connection != null);
        }
        RecordingDriver.Call call = RecordingDriver.CALLS.get(0);
        assertEquals(URL, call.url());
        assertEquals(USER, call.properties().getProperty("user"));
        assertFalse(call.properties().containsKey("password"));
    }

    @Test
    void omittedUsernameConnectsWithoutThatKey() throws Exception {
        DataSourceConfig config = DataSourceConfig.builder()
                .urlString(URL)
                .password(PASSWORD)
                .build();
        try (Connection connection = config.getDatasource().getConnection()) {
            assertTrue(connection != null);
        }
        RecordingDriver.Call call = RecordingDriver.CALLS.get(0);
        assertEquals(URL, call.url());
        assertEquals(PASSWORD, call.properties().getProperty("password"));
        assertFalse(call.properties().containsKey("user"));
    }

    private static Class<?> findLoaded(String name) throws ReflectiveOperationException {
        Method method = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
        method.setAccessible(true);
        return (Class<?>) method.invoke(DataSourceConfig.class.getClassLoader(), name);
    }
}
