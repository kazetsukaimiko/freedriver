package io.freedriver.inotify.linux;

import jnr.ffi.LibraryLoader;
import jnr.ffi.annotations.Out;
import jnr.ffi.types.size_t;
import jnr.ffi.types.ssize_t;

interface LinuxCLib {
    LinuxCLib INSTANCE = LibraryLoader.create(LinuxCLib.class)
            .failImmediately()
            .load("c");

    int inotify_init();

    int inotify_add_watch(int fd, String pathname, int mask);

    int inotify_rm_watch(int fd, int wd);

    @ssize_t
    long read(int fd, @Out byte[] buffer, @size_t long count);

    int close(int fd);
}