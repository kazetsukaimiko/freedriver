package io.freedriver.jsonlink.config.v2;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import io.freedriver.jsonlink.config.Migration;
import io.freedriver.jsonlink.config.v3.ApplianceDescriptor;
import io.freedriver.jsonlink.config.v3.EventDescriptor;
import io.freedriver.jsonlink.config.v3.JoystickButtonEvent;
import io.freedriver.jsonlink.config.v3.Mappings;
import io.freedriver.jsonlink.config.v3.ToggleAction;
import lombok.Builder;

@Deprecated
@Builder(toBuilder = true)
public record Mapping(
        UUID connectorId,
        String connectorName,
        List<Appliance> appliances,
        Map<String, List<String>> controlMap,
        Set<AnalogSensor> analogSensors,
        List<AnalogAlert> analogAlerts) implements Migration<Mappings> {
    public Mapping {
        appliances = appliances == null ? List.of() : List.copyOf(appliances);
        controlMap = controlMap == null ? Map.of() : Map.copyOf(controlMap);
        analogSensors = analogSensors == null ? Set.of() : Set.copyOf(analogSensors);
        analogAlerts = analogAlerts == null ? List.of() : List.copyOf(analogAlerts);
    }

    @Override
    public Mappings migrate() {
        Map<ApplianceDescriptor, io.freedriver.jsonlink.config.v3.Appliance> appliancesByName = new LinkedHashMap<>();
        appliances.forEach(appliance -> appliancesByName.put(
                new ApplianceDescriptor(appliance.name()),
                appliance.migrate(connectorId)
        ));

        Map<EventDescriptor, io.freedriver.jsonlink.config.v3.ControlEvent> controlEvents = new LinkedHashMap<>();
        Map<EventDescriptor, List<ToggleAction>> toggleActions = new LinkedHashMap<>();
        controlMap.forEach((jsEventDesc, applianceNames) -> {
            String[] split = jsEventDesc.split(":");
            EventDescriptor descriptor = new EventDescriptor(String.join("_AND_", applianceNames));
            JoystickButtonEvent buttonEvent = JoystickButtonEvent.builder()
                    .button(Integer.parseInt(split[0]))
                    .buttonState(Objects.equals("0", split[1])
                            ? JoystickButtonEvent.ButtonState.RELEASE
                            : JoystickButtonEvent.ButtonState.PRESS)
                    .build();
            controlEvents.put(descriptor, buttonEvent);
            toggleActions.put(descriptor, applianceNames.stream().map(ToggleAction::new).collect(Collectors.toList()));
        });
        return new Mappings(appliancesByName, controlEvents, toggleActions);
    }
}
