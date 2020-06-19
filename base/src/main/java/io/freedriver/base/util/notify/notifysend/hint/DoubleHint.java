package io.freedriver.base.util.notify.notifysend.hint;

public class DoubleHint extends Hint<Double> {
    protected DoubleHint(String name, Double value) {
        super(name, value);
    }

    @Override
    protected Type getType() {
        return Type.DOUBLE;
    }
}
