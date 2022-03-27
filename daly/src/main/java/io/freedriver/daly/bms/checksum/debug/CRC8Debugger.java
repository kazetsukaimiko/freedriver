package io.freedriver.daly.bms.checksum.debug;

public interface CRC8Debugger {
    void append(int start, int component, int end);

    static void noop(int start, int component, int end) {
    }
}
