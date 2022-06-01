module freedriver.genetry {
    exports io.freedriver.generty;
    exports io.freedriver.generty.model;
    opens io.freedriver.generty.model;
    requires java.logging;
    requires io.freedriver.math;
    requires io.freedriver.base;
    requires io.freedriver.discovery;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
}