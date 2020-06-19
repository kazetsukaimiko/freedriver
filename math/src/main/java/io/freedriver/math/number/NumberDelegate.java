package io.freedriver.math.number;

public abstract class NumberDelegate extends Number {
    abstract public Number getDelegateNumber();

    @Override
    public int intValue() {
        return getDelegateNumber().intValue();
    }

    @Override
    public long longValue() {
        return getDelegateNumber().longValue();
    }

    @Override
    public float floatValue() {
        return getDelegateNumber().floatValue();
    }

    @Override
    public double doubleValue() {
        return getDelegateNumber().doubleValue();
    }
}
