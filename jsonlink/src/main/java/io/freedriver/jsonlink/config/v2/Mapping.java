package io.freedriver.jsonlink.config.v2;

import io.freedriver.jsonlink.config.Migration;
import io.freedriver.jsonlink.config.v3.ApplianceDescriptor;
import io.freedriver.jsonlink.config.v3.Mappings;
import io.freedriver.jsonlink.config.v3.EventDescriptor;
import io.freedriver.jsonlink.config.v3.JoystickButtonEvent;
import io.freedriver.jsonlink.config.v3.ToggleAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Deprecated
public class Mapping implements Migration<Mappings> {

    private UUID connectorId;
    private String connectorName;
    private List<Appliance> appliances = new ArrayList<>();
    private Map<String, List<String>> controlMap = new HashMap<>();
    private Set<AnalogSensor> analogSensors = new HashSet<>();
    private List<AnalogAlert> analogAlerts = new ArrayList<>();

    public Mapping() {
    }

    public UUID getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(UUID connectorId) {
        this.connectorId = connectorId;
    }

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public List<Appliance> getAppliances() {
        return appliances;
    }

    public void setAppliances(List<Appliance> appliances) {
        this.appliances = appliances;
    }

    public Map<String, List<String>> getControlMap() {
        return controlMap;
    }

    public void setControlMap(Map<String, List<String>> controlMap) {
        this.controlMap = controlMap;
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

    @Override
    public String toString() {
        return "Mapping{" +
                "connectorId=" + connectorId +
                ", connectorName='" + connectorName + '\'' +
                ", appliances=" + appliances +
                ", controlMap=" + controlMap +
                '}';
    }

    @Override
    public Mappings migrate() {
        Mappings combinedMapping = new Mappings();
        getAppliances()
                .forEach(appliance -> {
                    combinedMapping.getAppliances().put(
                            new ApplianceDescriptor(appliance.getName()),
                            appliance.migrate(getConnectorId())
                    );
                });

        getControlMap()
                .forEach((jsEventDesc, appliances) -> {
                    String[] split = jsEventDesc.split(":");
                    EventDescriptor descriptor = new EventDescriptor(String.join("_AND_", appliances));

                    JoystickButtonEvent buttonEvent = new JoystickButtonEvent();
                    buttonEvent.setButton(Integer.parseInt(split[0]));
                    buttonEvent.setButtonState(Objects.equals("0", split[1])
                        ? JoystickButtonEvent.ButtonState.RELEASE
                        : JoystickButtonEvent.ButtonState.PRESS);
                    combinedMapping.getControlEvents().put(descriptor, buttonEvent);

                    combinedMapping.getToggleActions().put(descriptor, appliances.stream().map(ToggleAction::new).collect(Collectors.toList()));
                });
        return combinedMapping;
    }
}
