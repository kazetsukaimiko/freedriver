package io.freedriver.daly.bms;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

public class RequestTest {
    @Test
    public void testRender() {
        Stream.of(QueryId.values())
                .map(ReadRequest::new)
                .forEach(this::issue);
    }

    private void issue(ReadRequest readRequest) {
        System.out.println("Sending:\n" + readRequest);

    }

}
