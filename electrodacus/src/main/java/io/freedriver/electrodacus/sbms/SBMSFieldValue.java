package io.freedriver.electrodacus.sbms;

public class SBMSFieldValue {
    private final SBMSField field;
    private final String value;

    public SBMSFieldValue(SBMSField field, String value) {
        this.field = field;
        this.value = value;
    }

    public SBMSField getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return field.name() + ": " + value;
    }
}
