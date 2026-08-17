package io.freedriver.jsonlink.config;

import lombok.Builder;

@Builder(toBuilder = true)
public record EventSequence(EventDuration duration, String group) {
}
