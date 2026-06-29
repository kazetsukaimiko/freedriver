package io.freedriver.discovery;

import java.time.Duration;
import java.util.Set;
import java.util.function.Predicate;

import io.freedriver.discovery.jmdns.JMDNSDiscovery;

public class Discover {
    private static final Discovery DISCOVERY = new JMDNSDiscovery();
    public static Set<DiscoveredService> find(ServiceType serviceType, Predicate<DiscoveredService> predicate, Duration maxWait) {
        return DISCOVERY.find(serviceType, predicate, maxWait);
    }
}
