package io.freedriver.daly.bms.checksum;

import io.freedriver.daly.bms.checksum.debug.CRC8Debugger;

import java.util.zip.Checksum;

/**
 * Calculate CRC-8 checksum
 */
public class CRC8 implements Checksum {
    private final CRC8Debugger debugger;
    private int crc;

    public CRC8() {
        this(null);
    }

    public CRC8(CRC8Debugger debugger) {
        this.debugger = debugger != null
            ? debugger
            : CRC8Debugger::noop;
    }

    public static byte calc(byte[] withCRC, int length, CRC8Debugger debugger) {
        CRC8 crc = new CRC8(debugger);
        crc.reset();
        crc.update(withCRC, 0, length);
        return (byte) crc.crc;
    }

    public static byte calc(byte[] withCRC, int length) {
        return calc(withCRC, length, null);
    }

    @Override
    public void update(int i) {
        int startCRC = crc;
        crc = (crc + (i & 0xFF)) & 0xFF;
        debugger.append(startCRC, i & 0xFF, crc);
    }

    @Override
    public void update(byte[] bytes, int offset, int leng) {
        for (int i = 0; i<leng; i++) {
            update(bytes[i+offset] & 0xFF);
        }
    }

    @Override
    public long getValue() {
        return crc & 0xFF;
    }

    @Override
    public void reset() {
        crc = 0;
    }
}
