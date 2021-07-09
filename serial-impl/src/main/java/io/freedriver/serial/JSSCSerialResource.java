package io.freedriver.serial;

import io.freedriver.serial.stream.api.PortReference;
import io.freedriver.serial.api.SerialResource;
import io.freedriver.serial.api.exception.SerialResourceException;
import io.freedriver.serial.api.params.SerialParams;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Iterator;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

@Deprecated
public class JSSCSerialResource implements SerialResource {
    private static final Logger LOGGER = Logger.getLogger(JSSCSerialResource.class.getName());

    private final Supplier<Boolean> validWhile;
    private final SerialPort serialPort;
    private final SerialParams serialParams;

    private JSSCSerialResource(Supplier<Boolean> validWhile, SerialPort serialPort, SerialParams serialParams) {
        this.validWhile = validWhile;
        this.serialPort = serialPort;
        this.serialParams = serialParams;
        ensurePortOpen();
    }

    /*
    private JSSCSerialResource(Path path, SerialParams serialParams) {
        this(new SerialPort(path.toAbsolutePath().toString()), serialParams);
    }*/

    public JSSCSerialResource(PortReference portReference, SerialParams serialParams) {
        this(() -> !portReference.isInvalid(), new SerialPort(portReference.getCanonical().toString()), serialParams);
    }

    public void ensurePortOpen() {
        if (!serialPort.isOpened()) {
            try {
                serialPort.openPort();
                serialPort.setParams(
                        serialParams.getBaudRate(),
                        serialParams.getDataBits(),
                        serialParams.getStopBits(),
                        serialParams.getParity()
                );
                Thread.sleep(1000);
                //clear();
            } catch (SerialPortException | InterruptedException e) {
                throw new SerialResourceException("Could not configure port " + serialPort.getPortName(), e);
            }
        }
    }



    @Override
    public Iterator<Character> iterator() {
        ensurePortOpen();
        return this;
    }

    @Override
    public boolean hasNext() {
        return isOpened() && validWhile.get();
    }

    @Override
    public Character next() {
        try {
            return (char) serialPort.readBytes(1)[0];
        } catch (SerialPortException e) {
            throw new SerialResourceException("Exception reading from SerialResource", e);
        }
    }

    @Override
    public byte nextByte() {
        try {
            return (byte) serialPort.readBytes(1)[0];
        } catch (SerialPortException e) {
            throw new SerialResourceException("Exception reading from SerialResource", e);
        }
    }

    @Override
    public Charset getCharset() {
        return StandardCharsets.UTF_8;
    }

    @Override
    public Duration getPoll() {
        return null;
    }


    @Override
    public boolean isClosed() {
        return !isOpened();
    }

    @Override
    public boolean isOpened() {
        return serialPort.isOpened() && validWhile.get();
    }

    @Override
    public void clear() {
        try {
            while (serialPort.getInputBufferBytesCount() > 0) {
                String buffer = serialPort.readString(serialPort.getInputBufferBytesCount());
                LOGGER.log(Level.WARNING, "Discarding serial input: \n" + buffer);
            }
        } catch (SerialPortException e) {
            throw new SerialResourceException("Exception clearing SerialResource", e);
        }
    }

    @Override
    public void write(byte[] array) {
        if (isOpened()) {
            try {
                serialPort.writeBytes(array);
            } catch (SerialPortException e) {
                throw new SerialResourceException("Exception writing to SerialResource", e);
            }
        }
    }

    @Override
    public String getName() {
        return serialPort.getPortName();
    }

    @Override
    public void close() throws Exception {
        if (serialPort.isOpened()) {
            serialPort.closePort();
        }
    }
}



