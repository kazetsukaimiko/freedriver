package io.freedriver.jsonlink;

import io.freedriver.jsonlink.jackson.schema.v1.Request;
import io.freedriver.jsonlink.jackson.schema.v1.Response;
import io.freedriver.serial.stream.api.SerialStream;

import java.io.IOException;
import java.time.Duration;

public class StreamConnector implements Connector {
    private final SerialStream stream;

    public StreamConnector(SerialStream stream) {
        this.stream = stream;
    }

    @Override
    public Response send(Request request, Duration maxwait) throws ConnectorException {
        try {
            MAPPER.writer().writeValue(stream.getOutputStream(), request);
            return MAPPER.reader().readValue(stream.getInputStream(), Response.class);
        } catch (IOException e) {
            throw new ConnectorException("Couldn't send request: ", e);
        }
    }

    @Override
    public String device() {
        return null;
    }

    @Override
    public boolean isClosed() {
        return stream.isClosed();
    }

    @Override
    public void close() throws Exception {
        stream.close();
    }
}
