package io.freedriver.ee.prop;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum  DeploymentProperties {
    NAME("deployment.name")
    ;

    private static final Logger LOGGER = Logger.getLogger(DeploymentProperties.class.getName());

    private final String propertyName;

    DeploymentProperties(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Optional<String> assign(String s) {
        LOGGER.log(Level.WARNING, "Assigning property: "+getPropertyName()+ "="+s);
        return Optional.ofNullable(s)
                .map(this::setProperty)
                .filter(this::isPresent);
    }

    public Optional<String> fetch() {
        LOGGER.log(Level.WARNING, "Fetching property: "+getPropertyName());
        return Optional.of(propertyName)
                .map(System::getProperty)
                .filter(this::isPresent);
    }

    private String setProperty(String s) {
        return System.setProperty(getPropertyName(), s);
    }

    private boolean isPresent(String s) {
        return s != null && !s.isEmpty() && !s.isBlank();
    }
}
