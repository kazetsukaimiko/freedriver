package io.freedriver.discovery;

import lombok.Builder;

@Builder(toBuilder = true)
public record DiscoveredService(String name, String dns, String status, String address) {
}
