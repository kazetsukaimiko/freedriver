package io.freedriver.jsonlink.config.v2;

import java.util.List;


/*
{
    "sensors": ["tank1", "tank2"],
    "matching": "ANY",
    "condition" : "LESS_THAN"
    "value": 20,
    "content": "Fresh tank level low"
}
 */
public class AnalogAlert {
    private List<String> sensors;
    private AlertMatching matching;
    private AnalogAlertCondition condition;
    private float value = -1f;
    private String content;

    public AnalogAlert() {
    }

    public List<String> getSensors() {
        return sensors;
    }

    public void setSensors(List<String> sensors) {
        this.sensors = sensors;
    }

    public AlertMatching getMatching() {
        return matching;
    }

    public void setMatching(AlertMatching matching) {
        this.matching = matching;
    }

    public AnalogAlertCondition getCondition() {
        return condition;
    }

    public void setCondition(AnalogAlertCondition condition) {
        this.condition = condition;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "AnalogAlert{" +
                "sensors=" + sensors +
                ", matching=" + matching +
                ", condition=" + condition +
                ", value=" + value +
                '}';
    }
}
