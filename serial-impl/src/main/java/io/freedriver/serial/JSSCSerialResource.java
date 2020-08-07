package io.freedriver.serial;

import io.freedriver.serial.api.exception.SerialResourceException;
import io.freedriver.serial.api.params.SerialParams;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSSCSerialResource implements SerialResource {
    private static final Logger LOGGER = Logger.getLogger(JSSCSerialResource.class.getName());

    private final SerialPort serialPort;
    private final SerialParams serialParams;

    public JSSCSerialResource(SerialPort serialPort, SerialParams serialParams) {
        this.serialPort = serialPort;
        this.serialParams = serialParams;
        ensurePortOpen();
    }

    public JSSCSerialResource(Path path, SerialParams serialParams) {
        this(new SerialPort(path.toAbsolutePath().toString()), serialParams);
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
    public Iterator<String> iterator() {
        ensurePortOpen();
        return this;
    }

    @Override
    public boolean hasNext() {
        return isOpened();
    }

    @Override
    public String next() {
        try {
            return serialPort.readString(1);
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
    public boolean isOpened() {
        return serialPort.isOpened();
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



