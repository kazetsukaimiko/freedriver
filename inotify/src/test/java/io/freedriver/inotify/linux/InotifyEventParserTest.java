package io.freedriver.inotify.linux;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import org.junit.jupiter.api.Test;

class InotifyEventParserTest {
    @Test
    void parsesSingleNamedEvent() {
        byte[] buffer = new byte[32];
        ByteBuffer header = ByteBuffer.wrap(buffer).order(ByteOrder.nativeOrder());
        header.putInt(7);
        header.putInt(0x00000100);
        header.putInt(0);
        header.putInt(16);
        byte[] name = "usb-example\0\0\0\0\0".getBytes();
        System.arraycopy(name, 0, buffer, 16, name.length);

        List<InotifyEventParser.ParsedInotifyEvent> events =
                InotifyEventParser.parse(buffer, 32);

        assertFalse(events.isEmpty());
        assertEquals(7, events.get(0).watchDescriptor());
        assertEquals("usb-example", events.get(0).name());
    }
}