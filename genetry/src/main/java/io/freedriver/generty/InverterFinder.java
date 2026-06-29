package io.freedriver.generty;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;

import io.freedriver.discovery.Discover;
import io.freedriver.discovery.DiscoveredService;
import io.freedriver.discovery.ServiceType;

public class InverterFinder {
    private static final String INVERTER_NAME = "Genetry Solar Inverter Local Webserver";

    private InverterFinder() {
    }

    public static Set<DiscoveredService> findInverters(Duration maxWait) {
        return Discover.find(ServiceType.HTTP_LOCAL, InverterFinder::filter, maxWait);
    }

    private static boolean filter(DiscoveredService discoveredService) {
        return Objects.equals(INVERTER_NAME, discoveredService.getName());
    }


}
