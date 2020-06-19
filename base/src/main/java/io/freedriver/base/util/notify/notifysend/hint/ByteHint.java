package io.freedriver.base.util.notify.notifysend.hint;

public class ByteHint extends Hint<Byte> {
    protected ByteHint(String name, Byte value) {
        super(name, value);
    }

    @Override
    protected Type getType() {
        return Type.BYTE;
    }
}
