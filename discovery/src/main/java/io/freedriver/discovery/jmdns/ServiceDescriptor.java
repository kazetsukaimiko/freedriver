package io.freedriver.discovery.jmdns;

import io.freedriver.discovery.ApplicationProtocol;
import io.freedriver.discovery.Scope;
import io.freedriver.discovery.TransportProtocol;

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
