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
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JMDNSDiscovery implements Discovery {
    private static final Logger LOGGER = Logger.getLogger(JMDNSDiscovery.class.getName());
    private static final ExecutorService POOL = Executors.newCachedThreadPool();
    @Override
    public Set<DiscoveredService> find(ServiceType serviceType, Predicate<DiscoveredService> filter, Duration maxWait) {
        List<Future<Set<DiscoveredService>>> futures = allAddresses()
                .map(inetAddress -> spawn(inetAddress, serviceType, filter, maxWait))
                .collect(Collectors.toList());

        return futures
                .stream()
                .map(future -> getOrLog(future, maxWait))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }



    private static <T> Stream<T> enumerationAsStream(Enumeration<T> e) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        new Iterator<T>() {
                            public T next() {
                                return e.nextElement();
                            }
                            public boolean hasNext() {
                                return e.hasMoreElements();
                            }
                        },
                        Spliterator.ORDERED), false);
    }

    private Set<DiscoveredService> getOrLog(Future<Set<DiscoveredService>> future, Duration maxWait) {
        try {
            return future.get(maxWait.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOGGER.log(Level.WARNING, "Couldn't Discover Services", e);
            return Collections.emptySet();
        }
    }

    public static Stream<InetAddress> allAddresses()  {
        try {
            return enumerationAsStream(NetworkInterface.getNetworkInterfaces())
                    .map(NetworkInterface::getInetAddresses)
                    .flatMap(JMDNSDiscovery::enumerationAsStream);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Couldn't get local host, discovery will fail", e);
            return Stream.empty();
        }
    }

    private Future<Set<DiscoveredService>> spawn(InetAddress inetAddress, ServiceType serviceType, Predicate<DiscoveredService> filter, Duration maxWait) {
        return POOL.submit(() -> {
            try (AddressesCatcher catcher = createCatcher(inetAddress, serviceType, filter)) {
                Thread.sleep(maxWait.toMillis());
                return catcher.getServices();
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                LOGGER.log(Level.WARNING, "Couldn't Discover Services", e);
                return Collections.emptySet();
            }
        });
    }

    private AddressesCatcher createCatcher(InetAddress inetAddress, ServiceType serviceType,  Predicate<DiscoveredService> filter) throws IOException {
        // Create a JmDNS instance
        JmDNS jmdns = JmDNS.create(inetAddress);
        AddressesCatcher catcher = new AddressesCatcher(jmdns, filter);
        // Add a service listener
        jmdns.addServiceListener(serviceType.toString(), catcher);
        return catcher;
    }

    private interface DiscoveredServicesProvider {
        Set<DiscoveredService> getServices();
    }

    private static class AddressesCatcher implements ServiceListener, DiscoveredServicesProvider, AutoCloseable {
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

        @Override
        public Set<DiscoveredService> getServices() {
            return services;
        }

        @Override
        public void close() throws Exception {
            jmdns.close();
        }
    }
}
