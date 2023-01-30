module io.freedriver.clients.ipb {
    requires java.logging;
    requires io.freedriver.client;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires jakarta.ws.rs;
    requires lombok;
    opens io.freedriver.clients.ipb.model.request;
    opens io.freedriver.clients.ipb.model.core;
    opens io.freedriver.clients.ipb.model.response;
    opens io.freedriver.clients.ipb.model.forums;
    exports io.freedriver.clients.ipb.api;
    exports io.freedriver.clients.ipb;
    exports io.freedriver.clients.ipb.model.core;
    exports io.freedriver.clients.ipb.model.request;
    exports io.freedriver.clients.ipb.model.response;
    exports io.freedriver.clients.ipb.model.forums;
}