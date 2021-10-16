package io.freedriver.base.discovery;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServiceType {
    private TransportProtocol transportProtocol;
    private ApplicationProtocol applicationProtocol;
    private Scope scope;

    public ServiceType(TransportProtocol transportProtocol, ApplicationProtocol applicationProtocol, Scope scope) {
        this.transportProtocol = transportProtocol;
        this.applicationProtocol = applicationProtocol;
        this.scope = scope;
    }

    public ServiceType() {
    }

    public TransportProtocol getTransportProtocol() {
        return transportProtocol;
    }

    public void setTransportProtocol(TransportProtocol transportProtocol) {
        this.transportProtocol = transportProtocol;
    }

    public ApplicationProtocol getApplicationProtocol() {
        return applicationProtocol;
    }

    public void setApplicationProtocol(ApplicationProtocol applicationProtocol) {
        this.applicationProtocol = applicationProtocol;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return Stream.of(transportProtocol, applicationProtocol, scope)
                .map(NamedAspect::getName)
                .collect(Collectors.joining(".")) + ".";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceType that = (ServiceType) o;
        return Objects.equals(transportProtocol, that.transportProtocol) && Objects.equals(applicationProtocol, that.applicationProtocol) && Objects.equals(scope, that.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transportProtocol, applicationProtocol, scope);
    }
}
