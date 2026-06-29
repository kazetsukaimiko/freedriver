package io.freedriver.inotify.linux;

import jnr.ffi.LibraryLoader;
import jnr.ffi.annotations.Out;
import jnr.ffi.types.size_t;
import jnr.ffi.types.ssize_t;

/**
 * jnr-ffi bindings to libc inotify and I/O primitives.
 *
 * @see <a href="https://man7.org/linux/man-pages/man7/inotify.7.html">inotify(7)</a>
 */
interface LinuxCLib {
    LinuxCLib INSTANCE = LibraryLoader.create(LinuxCLib.class)
            .failImmediately()
            .load("c");

    /**
     * Initializes an inotify instance. See {@code inotify_init(2)}.
     *
     * @return a file descriptor, or {@code -1} on failure
     */
    int inotify_init();

    /**
     * Adds a watch for {@code pathname}. See {@code inotify_add_watch(2)}.
     *
     * @param fd inotify file descriptor from {@link #inotify_init()}
     * @param pathname directory path to watch
     * @param mask bitmask of {@code IN_*} flags
     * @return watch descriptor, or {@code -1} on failure
     */
    int inotify_add_watch(int fd, String pathname, int mask);

    /**
     * Removes a watch. See {@code inotify_rm_watch(2)}.
     *
     * @param fd inotify file descriptor
     * @param wd watch descriptor returned by {@link #inotify_add_watch(int, String, int)}
     * @return {@code 0} on success, or {@code -1} on failure
     */
    int inotify_rm_watch(int fd, int wd);

    /**
     * Reads inotify events from the file descriptor. See {@code read(2)}.
     *
     * @param fd inotify file descriptor
     * @param buffer destination buffer for one or more inotify event records
     * @param count buffer length
     * @return number of bytes read, or {@code -1} on failure
     */
    @ssize_t
    long read(int fd, @Out byte[] buffer, @size_t long count);

    /**
     * Closes the inotify file descriptor. See {@code close(2)}.
     *
     * @param fd inotify file descriptor
     * @return {@code 0} on success, or {@code -1} on failure
     */
    int close(int fd);
}