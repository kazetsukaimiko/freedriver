package io.freedriver.base.util.notify.notifysend.hint;

public class IntHint extends Hint<Integer> {
    protected IntHint(String name, Integer value) {
        super(name, value);
    }

    @Override
    protected Type getType() {
        return Type.INT;
    }
}
