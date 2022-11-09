package io.freedriver.discovery;

import io.freedriver.discovery.jmdns.JMDNSDiscovery;
import org.junit.jupiter.api.Test;

import java.net.Inet4Address;

public class JMDNSDiscoveryTest {
    @Test
    public void testGetAllAddresses() {
        JMDNSDiscovery.allAddresses()
                .filter(Inet4Address.class::isInstance)
                .forEach(System.out::println);
    }
}
