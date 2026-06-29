package io.freedriver.inotify;

import java.util.ServiceLoader;
import java.util.stream.Stream;

/**
 * SPI for filesystem notifications from an {@link InotifyWatcher}.
 *
 * <p>Register providers via {@code META-INF/services/} and discover them with {@link #load()}.
 */
@FunctionalInterface
public interface InotifyListener {
    void onEvent(InotifyEvent event);

    /**
     * Discovers registered {@link InotifyListener} providers.
     *
     * <p>Uses this interface's defining class loader, not the current thread context class loader.
     */
    static Stream<InotifyListener> load() {
        return ServiceLoader.load(InotifyListener.class, InotifyListener.class.getClassLoader())
                .stream()
                .map(ServiceLoader.Provider::get);
    }
}