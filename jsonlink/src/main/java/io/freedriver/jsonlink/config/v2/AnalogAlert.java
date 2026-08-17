package io.freedriver.jsonlink.config.v2;

import java.util.List;

import lombok.Builder;

/*
{
    "sensors": ["tank1", "tank2"],
    "matching": "ANY",
    "condition" : "LESS_THAN"
    "value": 20,
    "content": "Fresh tank level low"
}
 */
@Builder(toBuilder = true)
public record AnalogAlert(
        List<String> sensors,
        AlertMatching matching,
        AnalogAlertCondition condition,
        float value,
        String content) {
    public AnalogAlert {
        sensors = sensors == null ? List.of() : List.copyOf(sensors);
    }

    public static class AnalogAlertBuilder {
        private float value = -1f;
    }
}
