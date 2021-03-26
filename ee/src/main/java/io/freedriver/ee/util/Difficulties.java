package io.freedriver.ee.util;

import java.lang.reflect.Array;

/**
 * Odds and ends, pains in the back, the horrifically mundane.
 */
public class Difficulties {
    public static <T> T[] merge(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    public static <T> T[] append(T[] a, T... b) {
        return merge(a, b);
    }

    public static <T> T[] prepend(T[] a, T... b) {
        return merge(b, a);
    }
}
