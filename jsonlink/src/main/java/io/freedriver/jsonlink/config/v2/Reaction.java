package io.freedriver.jsonlink.config.v2;

import lombok.Builder;

@Builder(toBuilder = true)
public record Reaction(String coordinate, String appliance) {
}
