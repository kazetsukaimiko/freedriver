package io.freedriver.inotify.linux;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

final class InotifyEventParser {
    private static final int HEADER_SIZE = 16;

    private InotifyEventParser() {
    }

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

    static final class ParsedInotifyEvent {
        private final int watchDescriptor;
        private final int mask;
        private final int cookie;
        private final String name;

        ParsedInotifyEvent(int watchDescriptor, int mask, int cookie, String name) {
            this.watchDescriptor = watchDescriptor;
            this.mask = mask;
            this.cookie = cookie;
            this.name = name;
        }

        int watchDescriptor() {
            return watchDescriptor;
        }

        int mask() {
            return mask;
        }

        int cookie() {
            return cookie;
        }

        String name() {
            return name;
        }
    }
}