package io.freedriver.ee.discovery.jmdns;

import io.freedriver.ee.discovery.ApplicationProtocol;
import io.freedriver.ee.discovery.Scope;
import io.freedriver.ee.discovery.TransportProtocol;

import javax.jmdns.ServiceInfo;

public class ServiceDescriptor implements JMDNSAspect<ServiceInfo> {
    private TransportProtocol transportProtocol;
    private ApplicationProtocol applicationProtocol;
    private Scope scope;

    @Override
    public ServiceInfo asJMDNS() {
        return null;
    }
}
