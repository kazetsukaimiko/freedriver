package io.freedriver.serial.params;

public enum Parities implements Parity {
    PARITY_NONE(0),
    PARITY_ODD(1),
    PARITY_EVEN(2),
    PARITY_MARK(3),
    PARITY_SPACE(4);

    private final int parity;

    Parities(int parity) {
        this.parity = parity;
    }

    public int getParity() {
        return parity;
    }
}
