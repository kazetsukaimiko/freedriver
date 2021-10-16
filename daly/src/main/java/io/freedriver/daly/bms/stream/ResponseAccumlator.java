package io.freedriver.daly.bms.stream;

import io.freedriver.base.util.ByteArrayBuilder;
import io.freedriver.daly.bms.Address;
import io.freedriver.daly.bms.DalyCommand;
import io.freedriver.daly.bms.QueryId;
import io.freedriver.daly.bms.Flag;
import io.freedriver.daly.bms.Response;
import io.freedriver.serial.stream.api.Accumulator;
import io.freedriver.serial.stream.api.SerialStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiPredicate;

public class ResponseAccumlator implements Accumulator<Response>, BiPredicate<Byte, ByteArrayBuilder> {
    /**
     * Reads Responses from a SerialStream
     */
    @Override
    public Response apply(SerialStream stream) throws IOException {
        return ofFullMessage(stream.readFor(DalyCommand.READ::matches, this));
    }

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

    /**
     * Tests whether we have a complete response packet from the BMS.
     * If we have the dataLength byte, use it to determine the expected length of the packet and compare.
     * If not, packet is incomplete.
     */
    @Override
    public boolean test(Byte aByte, ByteArrayBuilder byteArrayBuilder) {
        /*
        int dataLength = byteArrayBuilder.getPosition() >= 3 ? byteArrayBuilder.getUnderlying()[3] : -1;
        return dataLength >= 0 &&
                byteArrayBuilder.getPosition() >= 4 + dataLength + 1;

         */
        return (Objects.equals(Flag.END.getValue(), aByte));
    }
}
