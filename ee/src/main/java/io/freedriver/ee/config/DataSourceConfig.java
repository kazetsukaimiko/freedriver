package io.freedriver.ee.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Driver;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class DataSourceConfig {
    @JsonProperty("javax.persistence.jdbc.jndi")
    private String jndi;
    @JsonProperty("javax.persistence.jdbc.url")
    private String urlString;
    @JsonProperty("javax.persistence.jdbc.username")
    private String username;
    @JsonProperty("javax.persistence.jdbc.password")
    private String password;
    @JsonProperty("javax.persistence.jdbc.driver")
    private String driverClassName;
    @JsonProperty("javax.persistence.jdbc.properties")
    private Map<String, String> driverProperties;


    public DataSourceConfig() {
    }

    public String getJndi() {
        return jndi;
    }

    public void setJndi(String jndi) {
        this.jndi = jndi;
    }

    public String getUrlString() {
        return urlString;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public Map<String, String> getDriverProperties() {
        return driverProperties != null
                ? driverProperties
                : Collections.emptyMap();
    }

    public void setDriverProperties(Map<String, String> driverProperties) {
        this.driverProperties = driverProperties;
    }

    @JsonIgnore
    public Properties getProperties() {
        Properties properties = new Properties();
        getDriverProperties()
                .forEach(properties::put);
        return properties;
    }

    @JsonIgnore
    public URL getURL() throws MalformedURLException {
        return new URL(getUrlString());
    }

    @JsonIgnore
    public URI getURI() {
        URI uri = URI.create(getUrlString());
        if (getUsername() != null) {
            String auth = getUsername();
            if (getPassword() != null) {
                auth = auth + ":" + getPassword();
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
        return (Class<? extends Driver>) Class.forName(getDriverClassName());
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
