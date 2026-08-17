package io.freedriver.jsonlink.config.v3;

import java.util.List;
import java.util.Set;

import io.freedriver.jsonlink.config.v2.AnalogAlert;
import io.freedriver.jsonlink.config.v2.AnalogSensor;
import lombok.Builder;

@Builder(toBuilder = true)
public record SensorMapping(Set<AnalogSensor> analogSensors, List<AnalogAlert> analogAlerts) {
    public SensorMapping {
        analogSensors = analogSensors == null ? Set.of() : Set.copyOf(analogSensors);
        analogAlerts = analogAlerts == null ? List.of() : List.copyOf(analogAlerts);
    }
}
