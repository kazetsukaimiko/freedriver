package io.freedriver.discovery;

import lombok.Builder;

@Builder(toBuilder = true)
public record ServiceType(
        ApplicationProtocols applicationProtocol,
        TransportProtocol transportProtocol,
        CommonScopes scope) {

    public static final ServiceType HTTP_LOCAL = new ServiceType(ApplicationProtocols.HTTP, TransportProtocol.TCP, CommonScopes.LOCAL);

    @Override
    public String toString() {
        return String.join(".",
                applicationProtocol().getName(),
                transportProtocol().getName(),
                scope().getName()) + ".";
    }
}
