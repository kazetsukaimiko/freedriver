package io.freedriver.discovery;

import java.net.Inet4Address;

import io.freedriver.discovery.jmdns.JMDNSDiscovery;
import org.junit.jupiter.api.Test;

public class JMDNSDiscoveryTest {
    @Test
    public void testGetAllAddresses() {
        JMDNSDiscovery.allAddresses()
                .filter(Inet4Address.class::isInstance)
                .forEach(System.out::println);
    }
}
