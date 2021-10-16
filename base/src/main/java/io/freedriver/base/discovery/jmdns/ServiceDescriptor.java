package io.freedriver.base.discovery.jmdns;

import io.freedriver.base.discovery.ApplicationProtocol;
import io.freedriver.base.discovery.Scope;
import io.freedriver.base.discovery.TransportProtocols;

import javax.jmdns.ServiceInfo;

public class ServiceDescriptor implements JMDNSAspect<ServiceInfo> {
    private TransportProtocols transportProtocol;
    private ApplicationProtocol applicationProtocol;
    private Scope scope;

    @Override
    public ServiceInfo asJMDNS() {
        return null;
    }
}
