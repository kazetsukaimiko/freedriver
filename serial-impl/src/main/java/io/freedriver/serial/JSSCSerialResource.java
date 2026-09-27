package io.freedriver.serial;

import java.nio.file.Path;
import java.util.logging.Level;

import io.freedriver.serial.api.SerialResource;
import io.freedriver.serial.api.exception.SerialResourceException;
import io.freedriver.serial.api.params.SerialParams;
import jssc.SerialPort;
import jssc.SerialPortException;
import lombok.extern.java.Log;

@Log
public class JSSCSerialResource implements SerialResource {
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

    /**
     * Waits for bytes with {@code FIONREAD} instead of {@code SerialPort.readBytes(int)}.
     * That native call blocks in {@code select} and, on error or after {@code closePort},
     * loops without returning, so the file description and {@code TIOCEXCL} lock can outlive
     * {@code close()}. A handshake timeout then leaves the device busy for the next open.
     */
    @Override
    public byte[] read(int size) {
        try {
            while (true) {
                if (!serialPort.isOpened()) {
                    throw new SerialResourceException("Port closed " + serialPort.getPortName());
                }
                if (serialPort.getInputBufferBytesCount() >= size) {
                    return serialPort.readBytes(size);
                }
                Thread.sleep(20);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SerialResourceException("Interrupted reading from " + serialPort.getPortName(), e);
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
                log.log(Level.WARNING, "Discarding serial input: \n" + buffer);
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
