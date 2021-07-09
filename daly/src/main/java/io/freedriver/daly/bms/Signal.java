package io.freedriver.daly.bms;

import io.freedriver.daly.bms.checksum.CRC8;

import java.util.Objects;

public abstract class Signal {
    private Flag startFlag = Flag.START;
    private Address address;
    private CommandId commandId;
    private byte checksum;

    public Signal() {
    }

    public abstract int getDataLength();
    public abstract byte[] getData();

    public Flag getStartFlag() {
        return startFlag;
    }

    public void setStartFlag(Flag startFlag) {
        this.startFlag = startFlag;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public CommandId getCommandId() {
        return commandId;
    }

    public void setCommandId(CommandId commandId) {
        this.commandId = commandId;
    }

    public byte getChecksum() {
        return (byte) (checksum & 0xFF);
    }

    public void setChecksum(byte checksum) {
        this.checksum = checksum;
    }


    public byte[] toFullMessage() {
        byte[] fullMessage = new byte[4 + getDataLength() + 1];
        fullMessage[0] = getStartFlag().getValue();
        fullMessage[1] = getAddress().getValue();
        fullMessage[2] = getCommandId().getValue();
        fullMessage[3] = (byte) (getDataLength() & 0xF);
        for (int idx = 0; idx < getData().length; idx++) {
            fullMessage[4+idx] = getData()[idx];
        }
        fullMessage[4 + getDataLength()] = getChecksum();
        return fullMessage;
    }


    public boolean validates() {
        byte[] fullMessage = toFullMessage();
        return getChecksum() == CRC8.calc(fullMessage, fullMessage.length-1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Signal signal = (Signal) o;
        return startFlag == signal.startFlag && address == signal.address && commandId == signal.commandId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startFlag, address, commandId);
    }
}
