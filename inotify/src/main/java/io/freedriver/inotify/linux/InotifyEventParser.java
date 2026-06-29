package io.freedriver.inotify.linux;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses raw inotify event records from a {@code read(2)} buffer.
 *
 * <p>Each record has a fixed 16-byte header followed by a NUL-padded name field:
 * <pre>
 *   struct inotify_event {
 *       int wd;         // watch descriptor
 *       uint32_t mask;  // event mask (IN_* flags)
 *       uint32_t cookie; // cookie to pair MOVED_FROM / MOVED_TO
 *       uint32_t len;   // length of name field (may include padding)
 *       char name[];    // optional entry basename, NUL-terminated
 *   };
 * </pre>
 *
 * @see <a href="https://man7.org/linux/man-pages/man7/inotify.7.html">inotify(7)</a>
 */
final class InotifyEventParser {
    private static final int HEADER_SIZE = 16;

    private InotifyEventParser() {
    }

    /**
     * Parses as many complete event records as fit in {@code buffer[0:bytesRead)}.
     */
    static List<ParsedInotifyEvent> parse(byte[] buffer, int bytesRead) {
        List<ParsedInotifyEvent> events = new ArrayList<>();
        int offset = 0;
        while (offset + HEADER_SIZE <= bytesRead) {
            ByteBuffer header = ByteBuffer.wrap(buffer, offset, HEADER_SIZE).order(ByteOrder.nativeOrder());
            int watchDescriptor = header.getInt();
            int mask = header.getInt();
            int cookie = header.getInt();
            int nameLength = header.getInt();
            int recordLength = HEADER_SIZE + nameLength;
            if (offset + recordLength > bytesRead) {
                break;
            }
            String name = "";
            if (nameLength > 0) {
                int end = offset + HEADER_SIZE;
                while (end < offset + recordLength && buffer[end] != 0) {
                    end++;
                }
                name = new String(buffer, offset + HEADER_SIZE, end - offset - HEADER_SIZE, StandardCharsets.UTF_8);
            }
            events.add(new ParsedInotifyEvent(watchDescriptor, mask, cookie, name));
            offset += recordLength;
        }
        return events;
    }

    record ParsedInotifyEvent(int watchDescriptor, int mask, int cookie, String name) {}
}