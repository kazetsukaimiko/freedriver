package io.freedriver.base.discovery;

import org.junit.jupiter.api.Test;

import static io.freedriver.base.discovery.ApplicationProtocols._http;
import static io.freedriver.base.discovery.CommonScopes.local;
import static io.freedriver.base.discovery.TransportProtocols._tcp;

public class DiscoveryTest {
    @Test
    public void testDiscovery() {
        ServiceType type = new ServiceType(_tcp, _http, local);
        new Discovery(type, "Genetry Solar Inverter Local Webserver")
                .get()
                .forEach(System.out::println);
    }
}
