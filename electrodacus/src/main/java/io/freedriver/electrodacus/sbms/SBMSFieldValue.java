package io.freedriver.electrodacus.sbms;

import lombok.Builder;

@Builder(toBuilder = true)
public record SBMSFieldValue(SBMSField field, String value) {
    @Override
    public String toString() {
        return field.name() + ": " + value;
    }
}
