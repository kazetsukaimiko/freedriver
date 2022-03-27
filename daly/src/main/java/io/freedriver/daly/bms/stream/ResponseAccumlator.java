package io.freedriver.daly.bms.stream;

import io.freedriver.base.util.accumulator.Accumulator;
import io.freedriver.daly.bms.Address;
import io.freedriver.daly.bms.QueryId;
import io.freedriver.daly.bms.Response;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class ResponseAccumlator implements Accumulator<ByteArrayOutputStream, Response> {
    /**
     * Decodes a fullMessage byte array packet as a Response.
     * @param fullMessage
     * @return
     */
    public static Response ofFullMessage(byte[] fullMessage) {
        Response r = new Response();
        r.setAddress(Address.ofByte(fullMessage[1]));
        r.setQueryId(QueryId.ofByte(fullMessage[2]));
        r.setDataLength(fullMessage[3]);
        r.setData(Arrays.copyOfRange(fullMessage, 4, 4+r.getDataLength()));
        r.setChecksum(fullMessage[fullMessage.length-1]);
        return r;
    }

    @Override
    public boolean isComplete(ByteArrayOutputStream input) {
        return false;
    }

    @Override
    public Response convert(ByteArrayOutputStream input) {
        return null;
    }

    /**
     * Tests whether we have a complete response packet from the BMS.
     * If we have the dataLength byte, use it to determine the expected length of the packet and compare.
     * If not, packet is incomplete.
     */
    /*
    @Override
    public boolean test(Byte aByte, ByteArrayBuilder byteArrayBuilder) {
        /*
        int dataLength = byteArrayBuilder.getPosition() >= 3 ? byteArrayBuilder.getUnderlying()[3] : -1;
        return dataLength >= 0 &&
                byteArrayBuilder.getPosition() >= 4 + dataLength + 1;

         */
       // return (Objects.equals(Flag.END.getValue(), aByte));
 //   }
}
