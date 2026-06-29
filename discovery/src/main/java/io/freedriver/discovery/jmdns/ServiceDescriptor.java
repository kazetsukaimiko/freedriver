package io.freedriver.discovery.jmdns;

import javax.jmdns.ServiceInfo;

import io.freedriver.discovery.ApplicationProtocol;
import io.freedriver.discovery.Scope;
import io.freedriver.discovery.TransportProtocol;

public class ServiceDescriptor implements JMDNSAspect<ServiceInfo> {
    private TransportProtocol transportProtocol;
    private ApplicationProtocol applicationProtocol;
    private Scope scope;

    @Override
    public ServiceInfo asJMDNS() {
        return null;
    }
}
