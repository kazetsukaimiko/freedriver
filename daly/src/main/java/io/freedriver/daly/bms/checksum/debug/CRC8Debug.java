package io.freedriver.daly.bms.checksum.debug;

import java.util.zip.Checksum;

/**
 * Calculate CRC-8 checksum
 */
public class CRC8Debug { //implements Checksum {
    /*
    private CRC8Steps steps = new CRC8Steps();

    public static CRC8Steps calc(byte[] withCRC, int length) {
        CRC8Debug crc = new CRC8Debug();
        crc.reset();
        crc.update(withCRC, 0, length);
        return crc.steps;
    }

    @Override
    public void update(int i) {
        CRC8Step step = new CRC8Step();
        step.setStart(steps.getCrc());
        step.setComponent(i);
        step.setEnd(steps.getCrc() + i);
        steps.setCrc(step);
    }

    @Override
    public void update(byte[] bytes, int offset, int leng) {
        for (int i = 0; i<leng; i++) {
            update(bytes[i+offset]);
        }
    }

    @Override
    public long getValue() {
        return steps.getCrc();
    }

    @Override
    public void reset() {
        steps = new CRC8Steps();
    }

     */
}
