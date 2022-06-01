package io.freedriver.jsonlink.config.v3;

import io.freedriver.jsonlink.config.v2.AnalogAlert;
import io.freedriver.jsonlink.config.v2.AnalogSensor;
import io.freedriver.jsonlink.config.v2.Appliance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SensorMapping  {
    private Set<AnalogSensor> analogSensors = new HashSet<>();
    private List<AnalogAlert> analogAlerts = new ArrayList<>();

    public SensorMapping() {
    }

    public Set<AnalogSensor> getAnalogSensors() {
        return analogSensors;
    }

    public void setAnalogSensors(Set<AnalogSensor> analogSensors) {
        this.analogSensors = analogSensors;
    }

    public List<AnalogAlert> getAnalogAlerts() {
        return analogAlerts;
    }

    public void setAnalogAlerts(List<AnalogAlert> analogAlerts) {
        this.analogAlerts = analogAlerts;
    }
}
