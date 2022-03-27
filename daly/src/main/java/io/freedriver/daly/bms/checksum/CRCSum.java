package io.freedriver.daly.bms.checksum;

import io.freedriver.daly.bms.checksum.debug.CRC8Debugger;

/**
 * Interface to caclulate a checksum
 */
public interface CRCSum {
    int calc(byte[] withCRC, int length, CRC8Debugger debugger);
}
