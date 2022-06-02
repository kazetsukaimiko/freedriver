package io.freedriver.discovery.jmdns;

import io.freedriver.discovery.DiscoveredService;
import io.freedriver.discovery.Discovery;
import io.freedriver.discovery.ServiceType;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class JMDNSDiscovery implements Discovery {
    @Override
    public Set<DiscoveredService> find(ServiceType serviceType, Predicate<DiscoveredService> filter, Duration maxWait) {

        try {
            // Create a JmDNS instance
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());
            AddressesCatcher catcher = new AddressesCatcher(jmdns, filter);
            // Add a service listener
            jmdns.addServiceListener(serviceType.toString(), catcher);

            // Wait a bit
            Thread.sleep(maxWait.toMillis());

            return catcher.getServices();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }




    private static class AddressesCatcher implements ServiceListener {
        private final JmDNS jmdns;
        private final Predicate<DiscoveredService> filter;
        private final Set<DiscoveredService> services = new HashSet<>();

        private AddressesCatcher(JmDNS jmdns, Predicate<DiscoveredService> filter) {
            this.jmdns = jmdns;
            this.filter = filter;
        }

        @Override
        public void serviceAdded(ServiceEvent event) {
            jmdns.requestServiceInfo(event.getType(), event.getName());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            remove(service(event));
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            add(service(event));
        }

        private void remove(DiscoveredService service) {
            if (filter.test(service)) {
                services.remove(service);
            }
        }

        private void add(DiscoveredService service) {
            if (filter.test(service)) {
                services.add(service);
            }
        }

        private static DiscoveredService service(ServiceEvent event) {
            return new DiscoveredService(
                event.getName(),
                removeTrailingPeriod(event.getInfo().getServer()),
                null,
                event.getInfo().getURLs().length > 0 ? event.getInfo().getURLs()[0] : null
            );
        }

        private static String removeTrailingPeriod(String input) {
            return input != null && input.endsWith(".")
                    ? input.substring(0, input.length() - 1)
                    : input;
        }

        public Set<DiscoveredService> getServices() {
            return services;
        }
    }
}
