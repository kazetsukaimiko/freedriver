package io.freedriver.genetry.wifi;

import io.freedriver.base.discovery.Discoverable;
import io.freedriver.base.discovery.Discovery;
import io.freedriver.base.discovery.ServiceType;

import java.util.Set;
import java.util.stream.Collectors;

import static io.freedriver.base.discovery.ApplicationProtocols._http;
import static io.freedriver.base.discovery.CommonScopes.local;
import static io.freedriver.base.discovery.TransportProtocols._tcp;

public class WiFiBoard {
    public static final String GS = "Genetry Solar Inverter Local Webserver";
    public static final ServiceType type = new ServiceType(_tcp, _http, local);

    private final Discoverable discoverable;

    private WiFiBoard(Discoverable discoverable) {
        this.discoverable = discoverable;
    }

    public static Set<WiFiBoard> getLocalWifiBoards() {
        return new Discovery(type, GS).get()
                .stream()
                .map(WiFiBoard::new)
                .collect(Collectors.toSet());
    }




}
