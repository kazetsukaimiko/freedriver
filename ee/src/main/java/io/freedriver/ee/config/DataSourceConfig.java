package io.freedriver.ee.config;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import javax.sql.DataSource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder(toBuilder = true)
public record DataSourceConfig(
        @JsonProperty("jakarta.persistence.jdbc.jndi") String jndi,
        @JsonProperty("jakarta.persistence.jdbc.url") String urlString,
        @JsonProperty("jakarta.persistence.jdbc.username") String username,
        @JsonProperty("jakarta.persistence.jdbc.password") String password,
        @JsonProperty("jakarta.persistence.jdbc.driver") String driverClassName,
        @JsonProperty("jakarta.persistence.jdbc.properties") Map<String, String> driverProperties) {

    private static final Logger LOGGER = Logger.getLogger(DataSourceConfig.class.getName());

    public DataSourceConfig {
        driverProperties = driverProperties != null
                ? driverProperties
                : Collections.emptyMap();
    }

    @JsonIgnore
    public Properties getProperties() {
        Properties properties = new Properties();
        driverProperties.forEach(properties::put);
        return properties;
    }

    /**
     * Driver options plus the JDBC user and password. A null username or password leaves that key
     * unset. A non-null value replaces the same key from {@code driverProperties}.
     */
    @JsonIgnore
    public Properties connectionProperties() {
        Properties properties = getProperties();
        if (username != null) {
            properties.setProperty("user", username);
        }
        if (password != null) {
            properties.setProperty("password", password);
        }
        return properties;
    }

    @JsonIgnore
    public URL getURL() throws MalformedURLException {
        return new URL(urlString);
    }

    @JsonIgnore
    public URI getURI() {
        URI uri = URI.create(urlString);
        if (username != null) {
            String auth = username;
            if (password != null) {
                auth = auth + ":" + password;
            }
            try {
                return new URI(
                        uri.getScheme(),
                        auth,
                        uri.getHost(),
                        uri.getPort(),
                        uri.getPath(),
                        uri.getQuery(),
                        uri.getFragment()
                );
            } catch (URISyntaxException e) {
                throw new RuntimeException("Invalid URI:", e);
            }
        }
        return uri;
    }

    /**
     * The driver {@link DriverManager} has registered for {@code urlString}. {@code driverClassName}
     * is ignored so configuration cannot choose a class to load.
     */
    @JsonIgnore
    public Driver getDriver() throws SQLException {
        if (driverClassName != null && !driverClassName.isBlank()) {
            LOGGER.warning("jakarta.persistence.jdbc.driver is ignored");
        }
        return DriverManager.getDriver(urlString);
    }

    public DataSource getDatasource() {
        return new DataSourceConfigDataSource(this);
    }
}
