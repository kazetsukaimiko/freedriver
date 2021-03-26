package io.freedriver.ee.config;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.net.URI;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class DataSourceConfigDataSource implements DataSource {
    private final Logger LOGGER = Logger.getLogger(DataSourceConfigDataSource.class.getName());
    private final DataSourceConfig config;
    private PrintWriter logWriter;
    private int loginTimeout = 0;

    public DataSourceConfigDataSource(DataSourceConfig config) {
        this.config = config;
        this.logWriter = new PrintWriter(System.out);
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            Driver driver = config.getDriver();
            URI uri = config.getURI();
            return driver.connect(uri.toString(), config.getProperties());
        } catch (Exception e) {
            throw new SQLException("Couldn't connect: ", e);
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        config.setUsername(username);
        config.setPassword(password);
        return getConnection();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws SQLException {
        this.logWriter = printWriter;
    }

    @Override
    public void setLoginTimeout(int i) throws SQLException {
        this.loginTimeout = i;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return loginTimeout;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return LOGGER;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;
    }
}
