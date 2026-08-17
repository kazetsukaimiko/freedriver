package io.freedriver.jsonlink.config;

import java.util.List;

import lombok.Builder;

@Builder(toBuilder = true)
public record Conditions(Operator operator, List<Conditions> children, List<Condition> nodes) {
    public Conditions {
        children = children == null ? List.of() : List.copyOf(children);
        nodes = nodes == null ? List.of() : List.copyOf(nodes);
    }
}
