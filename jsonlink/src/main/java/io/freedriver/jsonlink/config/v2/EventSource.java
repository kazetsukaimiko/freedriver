package io.freedriver.jsonlink.config.v2;

import java.util.List;

import lombok.Builder;

@Builder(toBuilder = true)
public record EventSource(String source, List<Reaction> reactions) {
    public EventSource {
        reactions = reactions == null ? List.of() : List.copyOf(reactions);
    }
}
