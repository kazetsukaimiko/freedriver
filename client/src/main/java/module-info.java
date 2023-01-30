module io.freedriver.client {
    exports io.freedriver.client.jackson;
    exports io.freedriver.client;
    requires java.logging;
    requires jakarta.ws.rs;
    requires resteasy.client.api;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires jakarta.activation;
    requires com.fasterxml.jackson.jakarta.rs.json;
    requires resteasy.jackson2.provider;
}