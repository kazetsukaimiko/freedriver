package io.freedriver.base.util.notify.notifysend.hint;

public enum Type {
    INT, DOUBLE, STRING, BYTE;
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}