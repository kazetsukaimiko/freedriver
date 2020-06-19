package io.freedriver.base.util.notify.notifysend.hint;


/**
 * TYPE:NAME:VALUE
 * Specifies basic extra data to pass. Valid types are int, double, string and byte.
 */



public abstract class Hint<T> {
    private final String name;
    private T value;

    protected Hint(String name, T value) {
        this.name = name;
        this.value = value;
    }

    protected abstract Type getType();

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.join(":", getType().toString(), getName(), String.valueOf(getValue()));
    }


}
