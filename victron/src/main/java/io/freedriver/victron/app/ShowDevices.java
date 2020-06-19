package io.freedriver.victron.app;

import io.freedriver.base.cli.ConsoleTable;
import io.freedriver.victron.VEDirectColumn;
import io.freedriver.victron.VEDirectMessage;
import io.freedriver.victron.VEDirectReader;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShowDevices {
    public static void main(String[] args) throws IOException, InterruptedException {
        ConsoleTable<VEDirectMessage> table = new ConsoleTable<>(VEDirectMessage.class);
        if (args.length == 0) {
            Stream.of(VEDirectColumn.values())
                    .forEach(veDirectColumn ->
                            table.addObjectColumn(veDirectColumn.name(), veDirectColumn.getDefinition().getter()));
        } else {
            Stream.of(args)
                    .map(VEDirectColumn::byName)
                    .flatMap(Optional::stream)
                    .forEach(veDirectColumn ->
                            table.addObjectColumn(veDirectColumn.name(), veDirectColumn.getDefinition().getter()));
        }

        final Map<VEDirectReader, VEDirectMessage> cache =Collections.synchronizedMap(new LinkedHashMap<>());
        List<VEDirectReader> deviceList = VEDirectReader.allVEDirectDevices()
                .collect(Collectors.toList());
        List<Future<?>> workers = new ArrayList<>();
        ExecutorService devicepool = Executors.newFixedThreadPool(deviceList.size());
        deviceList.forEach(device -> workers.add(devicepool.submit(() -> device.readAsMessages().forEach(message -> cache.put(device, message)))));

        while (true) {
            table.renderKeyValue(
                     new ArrayList<>(cache.values()),
                    VEDirectColumn.SERIAL_NUMBER.name()
            );
            Thread.sleep(1000);
        }

    }
}
