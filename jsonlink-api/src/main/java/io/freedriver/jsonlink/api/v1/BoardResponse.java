package io.freedriver.jsonlink.api.v1;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * One response from a board. JSON property names are the component names. The record holds
 * what the board sent; a consumer that wants a receive time records it itself.
 */
public record BoardResponse(
        Version version,
        UUID uuid,
        UUID requestId,
        BoardInfo boardInfo,
        List<String> info,
        List<String> error,
        List<String> debug,
        Map<Identifier, Boolean> digital,
        List<AnalogResponse> analog) {

    public BoardResponse {
        info = info == null ? List.of() : List.copyOf(info);
        error = error == null ? List.of() : List.copyOf(error);
        debug = debug == null ? List.of() : List.copyOf(debug);
        digital = digital == null ? Map.of() : Map.copyOf(digital);
        analog = analog == null ? List.of() : List.copyOf(analog);
    }

    public static BoardResponse empty() {
        return new BoardResponse(null, null, null, null, null, null, null, null, null);
    }

    public BoardResponse logAnyErrors(Consumer<String> errorLogger) {
        error.forEach(errorLogger);
        return this;
    }
}
