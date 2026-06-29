package io.freedriver.victron.app;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import io.freedriver.base.cli.ConsoleTable;
import io.freedriver.serial.JSSCSerialResource;
import io.freedriver.serial.api.SerialResource;
import io.freedriver.serial.api.params.*;
import io.freedriver.victron.VEDirectColumn;
import io.freedriver.victron.VEDirectMessage;
import io.freedriver.victron.VEDirectMessageStream;

public class ShowDevices {
    public static void main(String[] args) throws IOException, InterruptedException {
        ConsoleTable<VEDirectMessage> table = new ConsoleTable<>(VEDirectMessage.class);
        if (args.length == 0) {
            Stream.of(VEDirectColumn.values())
                    .forEach(veDirectColumn ->
                            table.addObjectColumn(veDirectColumn.name(), veDirectColumn.getDefinition().getter()));
        } else {
            Stream.of(args)
                    .map(ShowDevices::openSerialResource)
                    .map(VEDirectMessageStream::new)
                    .map(messageStream -> messageStream.stream().findFirst())
                    .flatMap(Optional::stream)
                    .forEach(System.out::println);
        }

    }

    private static SerialResource openSerialResource(String s) {
        SerialParams params = new SerialParams()
                .setBaudRate(BaudRates.BAUDRATE_19200)
                .setDataBits(DataBits.DATABITS_8)
                .setStopBits(StopBits.STOPBITS_1);

        return new JSSCSerialResource(Paths.get(s), params);
    }
}
