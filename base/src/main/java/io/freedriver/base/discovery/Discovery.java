package io.freedriver.base.discovery;

import io.freedriver.base.util.Difficulties;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Discovery implements ServiceListener {
    private static final Logger LOGGER = Logger.getLogger(Discovery.class.getName());
    
    private final ServiceType type;
    private final String name;
    private final Set<Discoverable> discoverables = new HashSet<>();

    public Discovery(ServiceType type, String name) {
        this.type = type;
        this.name = name;
    }


    public Stream<JmDNS> allInterfaces() {
        try {
            return Difficulties.enumerationAsStream(NetworkInterface.getNetworkInterfaces())
                    .flatMap(this::ofNetworkInterface);
        } catch (SocketException e) {
            e.printStackTrace();
            return Stream.empty();
        }
    }

    private Stream<JmDNS> ofNetworkInterface(NetworkInterface networkInterface) {
        LOGGER.fine("Creating JmDNS listeners for NetworkInterface " + networkInterface.getName());
        return Difficulties.enumerationAsStream(networkInterface.getInetAddresses())
                .flatMap(this::ofInetAddress);
    }

    private Stream<JmDNS> ofInetAddress(InetAddress inetAddress) {

        try {
            if (inetAddress instanceof Inet4Address) {
                LOGGER.fine("Creating JmDNS listeners for InetAddress " + inetAddress.getHostAddress());
                return Stream.of(JmDNS.create(inetAddress));
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return Stream.empty();
    }


    public List<Discoverable> get() {
        return get(Duration.ZERO);
    }

    public List<Discoverable> get(Duration duration) {
        synchronized (this) {
            try {
                discoverables.removeIf(s -> true);
                LOGGER.fine("Getting all addresses");
                List<JmDNS> allInterfaces = allInterfaces().collect(Collectors.toList());
                LOGGER.fine("Adding listeners");
                allInterfaces.forEach(jmDNS -> jmDNS.addServiceListener(type.toString(), this));

                allInterfaces.forEach(jmDNS -> jmDNS.requestServiceInfo(type.toString(), name));
                LOGGER.fine("Waiting");
                Thread.sleep(duration.toMillis());
                ExecutorService pool = Executors.newFixedThreadPool(allInterfaces.size());
                LOGGER.fine("Closing " + allInterfaces.size() + " addresses.");
                List<Future<Boolean>> closed = allInterfaces.stream().map(jmDNS -> pool.submit(() -> {
                    try {
                        LOGGER.fine("Closing " + jmDNS.getHostName());
                        jmDNS.close();
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }))
                        .collect(Collectors.toList());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
        LOGGER.fine("Sorting " + discoverables.size() + " results");
        return discoverables.stream()
                .sorted(Discoverable::sort)
                .collect(Collectors.toList());
    }

    @Override
    public void serviceAdded(ServiceEvent event) {
        discoverables.add(fromServiceEvent(event));
        LOGGER.fine("Added: " + event.getInfo());
    }

    @Override
    public void serviceRemoved(ServiceEvent event) {
        discoverables.remove(fromServiceEvent(event));
        LOGGER.fine("Removed: " + event.getInfo());
    }

    @Override
    public void serviceResolved(ServiceEvent event) {
        discoverables.add(fromServiceEvent(event));
        LOGGER.info("Resolved: " + event.getInfo());
    }

    public Discoverable fromServiceEvent(ServiceEvent event) {
        Discoverable d = new Discoverable();
        d.setName(event.getName());
        d.setType(event.getType());
        ServiceInfo info = event.getInfo();
        if (info != null) {
            d.setApplication(info.getApplication());
            d.setDomain(info.getDomain());
            d.setPort(info.getPort());
            d.setIpv4Addresses(Stream.of(info.getInet4Addresses()).collect(Collectors.toSet()));
            d.setIpv6Addresses(Stream.of(info.getInet6Addresses()).collect(Collectors.toSet()));
            d.setServer(info.getServer());
            d.setPriority(info.getPriority());
            d.setProtocol(info.getProtocol());
            d.setSubtype(info.getSubtype());
            d.setWeight(info.getWeight());
            d.setText(info.getTextBytes());
        }
        return d;
    }

}
