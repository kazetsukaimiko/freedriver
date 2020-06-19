package io.freedriver.base.util.notify.notifysend.hint;

public class StringHint extends Hint<String> {
    protected StringHint(String name, String value) {
        super(name, value);
    }

    @Override
    protected Type getType() {
        return Type.STRING;
    }
}
