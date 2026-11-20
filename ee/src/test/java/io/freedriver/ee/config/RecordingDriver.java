package io.freedriver.ee.config;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * JDBC 4 test driver. The service file under {@code META-INF/services} registers it.
 */
public class RecordingDriver implements Driver {
    public static final String URL_PREFIX = "jdbc:freedriver-test:";
    public static final CopyOnWriteArrayList<Call> CALLS = new CopyOnWriteArrayList<>();

    public record Call(String url, Properties properties) {
    }

    @Override
    public Connection connect(String url, Properties info) {
        if (!acceptsURL(url)) {
            return null;
        }
        Properties copy = new Properties();
        if (info != null) {
            copy.putAll(info);
        }
        CALLS.add(new Call(url, copy));
        return RecordingConnection.create();
    }

    @Override
    public boolean acceptsURL(String url) {
        return url != null && url.startsWith(URL_PREFIX);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
}
