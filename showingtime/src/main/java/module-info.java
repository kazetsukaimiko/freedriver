/**
 *
 */
module io.freedriver.showingtime {
    requires java.ws.rs;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires io.freedriver.base;
    exports io.freedriver.showingtime.model;
    exports io.freedriver.showingtime.rest;
    opens io.freedriver.showingtime.model;
}