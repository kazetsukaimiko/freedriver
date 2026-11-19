package io.freedriver.ee.config;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

final class RecordingConnection {
    private RecordingConnection() {
    }

    static Connection create() {
        InvocationHandler handler = (proxy, method, args) -> {
            Class<?> type = method.getReturnType();
            if (type.equals(boolean.class)) {
                return false;
            }
            if (type.equals(int.class)) {
                return 0;
            }
            if (type.equals(void.class)) {
                return null;
            }
            if ("unwrap".equals(method.getName())) {
                throw new SQLException("unwrap");
            }
            return null;
        };
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[] {Connection.class},
                handler);
    }
}
