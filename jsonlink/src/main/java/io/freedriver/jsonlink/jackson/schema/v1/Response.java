package io.freedriver.jsonlink.jackson.schema.v1;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.freedriver.jsonlink.jackson.schema.base.Version;
import lombok.Builder;

@Builder(toBuilder = true)
public record Response(
        Version version,
        UUID uuid,
        UUID requestId,
        BoardInfo boardInfo,
        List<String> info,
        List<String> error,
        List<String> debug,
        Map<Identifier, Boolean> digital,
        List<AnalogResponse> analog,
        Instant created) {

    public Response {
        info = info == null ? List.of() : List.copyOf(info);
        error = error == null ? List.of() : List.copyOf(error);
        debug = debug == null ? List.of() : List.copyOf(debug);
        digital = digital == null ? Map.of() : Map.copyOf(digital);
        analog = analog == null ? List.of() : List.copyOf(analog);
        created = created == null ? Instant.now() : created;
    }

    public static Response empty() {
        return new Response(null, null, null, null, null, null, null, null, null, null);
    }

    public Response logAnyErrors(Consumer<String> errorLogger) {
        error.forEach(errorLogger);
        return this;
    }

    @JsonIgnore
    public Instant createdAt() {
        return created;
    }
}
