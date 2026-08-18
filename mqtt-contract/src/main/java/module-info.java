module io.freedriver.mqtt.contract {
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    exports io.freedriver.mqtt.contract;
    opens io.freedriver.mqtt.contract to com.fasterxml.jackson.databind;
}
