package io.freedriver.discovery;

public class ServiceType {
    public static final ServiceType HTTP_LOCAL = new ServiceType(ApplicationProtocols.HTTP, TransportProtocol.TCP, CommonScopes.LOCAL);
    private final ApplicationProtocols applicationProtocol;
    private final TransportProtocol transportProtocol;
    private final CommonScopes scope;

    public ServiceType(ApplicationProtocols applicationProtocol, TransportProtocol transportProtocol, CommonScopes scope) {
        this.applicationProtocol = applicationProtocol;
        this.transportProtocol = transportProtocol;
        this.scope = scope;
    }

    public ApplicationProtocols getApplicationProtocol() {
        return applicationProtocol;
    }

    public TransportProtocol getTransportProtocol() {
        return transportProtocol;
    }

    public CommonScopes getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return String.join(".",
                getApplicationProtocol().getName(),
                getTransportProtocol().getName(),
                getScope().getName()) + ".";
    }
}
