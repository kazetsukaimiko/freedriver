package io.freedriver.victron.hwtest;

import io.freedriver.serial.stream.api.LineListener;
import io.freedriver.victron.VEDirectColumnValueStreamer;
import io.freedriver.victron.VEDirectStreamer;
import io.freedriver.victron.VictronLink;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class VEDirectStreamerHWTest {

    @Test
    public void testBasicCols() throws IOException {
        VEDirectColumnValueStreamer columnValueStreamer = VEDirectColumnValueStreamer.allColumnStreamers()
                .findFirst()
                .orElseThrow(RuntimeException::new);
        columnValueStreamer.stream()

                .limit(5)
                .forEach(System.out::println);
    }

    @Test
    public void testBasic() throws IOException {
        VEDirectStreamer messageStreamer = VEDirectStreamer.allMessageStreamers()
                .findFirst()
                .orElseThrow(RuntimeException::new);
        messageStreamer.stream()
                .limit(5)
                .forEach(message -> {
                    System.out.println("Messages: " + messageStreamer.countRead());
                    System.out.println(message);
                });
    }


    @Test
    public void testGetProduct() throws IOException {
        VEDirectStreamer.allMessageStreamers()
                .findFirst()
                .map(streamer ->
                    streamer.getFirstMessage()
                            .getProductType() + " " + streamer.getFirstMessage().getSerialNumber())
                .ifPresent(System.out::println);
    }
}
