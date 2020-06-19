package io.freedriver.serial;

import jssc.SerialPort;
import jssc.SerialPortException;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface SerialResource extends Iterable<String>, Iterator<String>, AutoCloseable {

    /**
     * Clears the serial input buffer.
     */
    void clear();
    void write(byte[] array);
    String getName();
    boolean isOpened();
    byte nextByte();

    static SerialResource of(final SerialPort serialPort, int baudRate) {
        return new SerialResource() {
            private final Logger logger = Logger.getLogger(getClass().getName());
            @Override
            public Iterator<String> iterator() {
                if (!serialPort.isOpened()) {
                    try {
                        serialPort.openPort();
                        serialPort.setParams(
                                baudRate,
                                SerialPort.DATABITS_8,
                                SerialPort.STOPBITS_1,
                                SerialPort.PARITY_NONE
                        );
                    } catch (SerialPortException e) {
                        throw new SerialResourceException("Could not configure port " + serialPort.getPortName(), e);
                    }
                }
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
                        logger.log(Level.WARNING, "Discarding serial input: \n" + buffer);
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
        };
    }
}
