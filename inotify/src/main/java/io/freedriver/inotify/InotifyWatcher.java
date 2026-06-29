package io.freedriver.inotify;

import java.nio.file.Path;

public interface InotifyWatcher extends AutoCloseable {
    boolean isSupported();

    InotifyWatch watch(Path directory, int mask, InotifyListener listener);

    void start();

    void stop();

    @Override
    void close();
}