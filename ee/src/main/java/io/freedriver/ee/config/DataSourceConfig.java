package io.freedriver.ee.config;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Driver;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
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

    @JsonIgnore
    @SuppressWarnings("unchecked")
    public Class<? extends Driver> getDriverClass() throws ClassNotFoundException {
        return (Class<? extends Driver>) Class.forName(driverClassName);
    }

    @JsonIgnore
    public Driver getDriver() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return getDriverClass()
                .getConstructor()
                .newInstance();
    }

    public DataSource getDatasource() {
        return new DataSourceConfigDataSource(this);
    }
}
