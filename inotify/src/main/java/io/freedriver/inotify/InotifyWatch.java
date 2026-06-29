package io.freedriver.inotify;

import java.nio.file.Path;

public interface InotifyWatch extends AutoCloseable {
    Path directory();

    int mask();

    @Override
    void close();
}