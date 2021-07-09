package io.freedriver.daly.bms;

import io.freedriver.daly.bms.checksum.CRC8;

public class DalyCommand {

    private Flag startFlag = Flag.START;
    private Address address = Address.UPPER;
    private CommandId commandId;
    private int dataLength;
    private byte[] data;

    public static int dalyChecksum(byte[] command) {
        return CRC8.calc(command, command.length);
    }
}
