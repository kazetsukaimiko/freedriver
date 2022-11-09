module genetry.kibana {
    requires elasticsearch.java;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires io.freedriver.discovery;
    requires freedriver.genetry;
    requires org.apache.httpcomponents.httpcore;
    requires elasticsearch.rest.client;
    requires java.net.http;
    requires java.logging;
    requires java.desktop;
    exports io.freedriver.generty.kibana;
}