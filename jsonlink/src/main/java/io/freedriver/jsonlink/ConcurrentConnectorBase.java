package io.freedriver.jsonlink;

/**
 * ConcurrentConnector superclass that forces interaction with the delegate via map and apply.
 */
public abstract class ConcurrentConnectorBase implements Connector {
    private final Connector delegate;

    public ConcurrentConnectorBase(Connector delegate) {
        this.delegate = delegate;
    }

    protected synchronized final <E extends Exception, R> R map(UnsafeFunction<E, Connector, R> connectorTFunction) throws E {
        return connectorTFunction.apply(delegate);
    }

    protected final <E extends Exception> void apply(UnsafeConsumer<E, Connector> connectorConsumer) throws E {
        map(d -> {
            connectorConsumer.accept(d);
            return null;
        });
    }
    @FunctionalInterface
    protected interface UnsafeFunction<E extends Exception, T, R> {
        R apply(T var1) throws E;
    }

    protected interface UnsafeConsumer<E extends Exception, T> {
        void accept(T var1) throws E;
    }
}
