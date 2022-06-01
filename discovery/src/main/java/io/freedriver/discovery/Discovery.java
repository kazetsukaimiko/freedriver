package io.freedriver.discovery;

import java.time.Duration;
import java.util.Set;
import java.util.function.Predicate;

public interface Discovery {
    Set<DiscoveredService> find(ServiceType serviceType, Predicate<DiscoveredService> predicate, Duration maxWait);


}
