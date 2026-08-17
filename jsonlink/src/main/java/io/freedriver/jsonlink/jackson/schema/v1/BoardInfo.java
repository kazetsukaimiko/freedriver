package io.freedriver.jsonlink.jackson.schema.v1;

import java.util.List;

import lombok.Builder;

@Builder(toBuilder = true)
public record BoardInfo(List<Identifier> digitals, List<Identifier> analogs) {
    public BoardInfo {
        digitals = digitals == null ? List.of() : List.copyOf(digitals);
        analogs = analogs == null ? List.of() : List.copyOf(analogs);
    }
}
