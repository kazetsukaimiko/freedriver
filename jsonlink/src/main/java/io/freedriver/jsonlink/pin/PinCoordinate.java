package io.freedriver.jsonlink.pin;

import java.util.UUID;

import io.freedriver.jsonlink.jackson.schema.v1.Identifier;
import lombok.Builder;

/**
 * Represents a GPIO pin on a board with a known id.
 */
@Builder(toBuilder = true)
public record PinCoordinate(UUID boardId, Identifier identifier) {}
