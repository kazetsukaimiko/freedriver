package io.freedriver.victron.emulation;

import io.freedriver.serial.SerialListener;
import io.freedriver.serial.api.exception.SerialResourceException;
import io.freedriver.victron.ColumnGenerator;
import io.freedriver.victron.VEDirectColumn;
import io.freedriver.victron.VEDirectColumnValue;
import io.freedriver.victron.VEDirectDeviceType;
import io.freedriver.victron.VEDirectMessage;
import io.freedriver.victron.VEDirectReader;
import io.freedriver.victron.VictronProduct;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class EmulatedTest {
    private final Logger LOGGER = Logger.getLogger(EmulatedTest.class.getName());

    @Test
    public void testEmulatedLineData() {
        ColumnEmulator columnEmulator = new ColumnEmulator(all().map(ColumnGenerator::create));
        for (String line : new SerialListener<String>(columnEmulator, SerialListener<String>::nextLine)) {
            LOGGER.info(line);
        }
        // Test AutoCloseable.
        try (Stream<String> lines = new SerialListener<>(columnEmulator, SerialListener<String>::nextLine).stream()) {
                lines
                    .forEach(LOGGER::info);
        }
    }

    @Test
    public void testEmulatingColumns() {
        ColumnEmulator columnEmulator = new ColumnEmulator(all().map(ColumnGenerator::create));
        StringBuilder sb = new StringBuilder();
        while (columnEmulator.hasNext()) {
            sb.append(columnEmulator.next());
        }

        assertThrows(SerialResourceException.class, columnEmulator::next,
                "Try throwing SRE.");

        LOGGER.info(sb.toString());
    }

    @Test
    public void testBuildMessages() {
        VEDirectMessage message = new VEDirectMessage();
        List<VEDirectColumnValue> generated = all().map(ColumnGenerator::create).collect(Collectors.toList());
        ColumnEmulator columnEmulator = new ColumnEmulator(generated.stream());
        SerialListener<VEDirectMessage> messages = new SerialListener<>(columnEmulator, VEDirectMessage.accumulator());
        VEDirectMessage comparison = messages.next();
        generated.forEach(columnValue ->assertEquals(
                    columnValue.getValue(),
                    columnValue.getColumn().getDefinition().getter().apply(comparison)));

        assertNotEquals(new VEDirectMessage(), comparison, "Comparison should populate");

        VEDirectMessage copy = new VEDirectMessage(comparison);
        assertEquals(comparison, copy, "equals should show equality between copies");

        Set<VEDirectMessage> uniqueMessages = new HashSet<>();
        uniqueMessages.add(comparison);
        uniqueMessages.add(copy);
        assertEquals(1, uniqueMessages.size(), "HashCode should show equality between copies");

        assertNotNull(comparison.toString(), "toString should function");

        Instant now = Instant.now();
        copy.setTimestamp(now);
        assertNotEquals(comparison, copy, "Changing any field breaks equality");
    }

    @Test
    public void testEachColumn() {
        all().forEach(columnGenerator -> {

            VEDirectColumnValue expectedValue = columnGenerator.create();
            LOGGER.info(expectedValue.toString());

            ColumnEmulator columnEmulator = new ColumnEmulator(expectedValue);
            SerialListener<VEDirectColumnValue> columnListener = new SerialListener<>(columnEmulator, VEDirectColumnValue.accumulator());
            VEDirectColumnValue comparison = columnListener.next();

            assertEquals(expectedValue, comparison,
                    "SerialListener should produce identical column output");
        });
    }


    @Test
    public void testAllColumnsAreTested() {
        Stream.of(VEDirectColumn.values())
                .forEach(column -> {
                    assertTrue(ColumnGenerator.ofVEDirectColumn(column).isPresent(),
                            column.name() + " needs to be tested");
                });
    }

    @Test
    public void testFindingProduct() {
        VictronProduct.all()
                .forEach(victronProductType -> {
                    String serialNumber = UUID.randomUUID().toString();
                    VEDirectReader veDirectDevice = emulatedVEDevice(new ColumnEmulator(Stream.of(
                            new VEDirectColumnValue(VEDirectColumn.PRODUCT_ID, victronProductType.getProductIdHex()),
                            new VEDirectColumnValue(VEDirectColumn.SERIAL_NUMBER, serialNumber))));
                    veDirectDevice.attemptToGetProduct()
                            .ifPresentOrElse(victronProduct -> {
                                assertEquals(serialNumber, victronProduct.getSerialNumber());
                                assertEquals(victronProductType, victronProduct.getType());
                            }, () -> fail("Couldn't get Victron Product."));
                });
    }


    @Test
    public void testReadingColumns() {
        VictronProduct.all()
                .forEach(victronProductType -> {
                    String serialNumber = UUID.randomUUID().toString();
                    List<VEDirectColumnValue> inputColumns = Stream.of(
                            new VEDirectColumnValue(VEDirectColumn.PRODUCT_ID, victronProductType.getProductIdHex()),
                            new VEDirectColumnValue(VEDirectColumn.SERIAL_NUMBER, serialNumber)).collect(Collectors.toList());
                    VEDirectReader veDirectDevice = emulatedVEDevice(new ColumnEmulator(inputColumns.stream()));
                    List<VEDirectColumnValue> outputColumns = veDirectDevice.readAsColumns()
                            .collect(Collectors.toList());
                    inputColumns.forEach(inputColumn -> assertTrue(outputColumns.contains(inputColumn)));
                    outputColumns.forEach(outputColumn -> assertTrue(inputColumns.contains(outputColumn)));
                });
    }

    @Test
    public void testReadingStringLines() {
    }

    Stream<ColumnGenerator> all() {
        return Stream.of(ColumnGenerator.values());
    }

    VEDirectReader emulatedVEDevice(ColumnEmulator columnEmulator) {
        VEDirectReader emulated = new VEDirectReader(VEDirectDeviceType.EMULATED_DEVICE, () -> columnEmulator);
        assertEquals(VEDirectDeviceType.EMULATED_DEVICE, emulated.getType());
        return emulated;
    }
}
