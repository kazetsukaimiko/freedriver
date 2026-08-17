package io.freedriver.jsonlink.jackson.schema.base;

import lombok.Builder;

/**
 * The base-level response object, including the version of the schema.
 */
@Builder(toBuilder = true)
public record BaseResponse(Version version) {}
