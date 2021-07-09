package io.freedriver.serial.stream;

import io.freedriver.serial.api.AutoCloseableWithState;
import io.freedriver.serial.api.exception.SerialResourceException;
import io.freedriver.serial.api.params.SerialParams;
import io.freedriver.serial.stream.api.PortReference;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class JSSCPort implements AutoCloseableWithState, SerialPortEventListener {
    private static final Map<PortReference, JSSCPort> PORTS_OPEN = new ConcurrentHashMap<>();

    private final PortReference portReference;
    private final SerialPort serialPort;
    private final SerialParams serialParams;
    private boolean closed = true;

    private JSSCPort(PortReference portReference, SerialPort serialPort, SerialParams serialParams) {
        this.portReference = portReference;
        this.serialPort = serialPort;
        this.serialParams = serialParams;
        ensurePortOpen();
    }

    private JSSCPort(PortReference portReference, SerialParams serialParams) {
        this(portReference, new SerialPort(portReference.getCanonical().toString()), serialParams);
    }

    private static synchronized void cullClosed() {
        List<PortReference> expired = PORTS_OPEN.keySet()
                .stream()
                .filter(pr -> PORTS_OPEN.get(pr).isClosed())
                .collect(Collectors.toList());
        expired.forEach(expiredPortRef -> {
            System.out.println("Culling closed Port Reference: " + expiredPortRef);
            PORTS_OPEN.remove(expiredPortRef);
        });
    }

    public static synchronized JSSCPort get(PortReference portReference, final SerialParams serialParams) {
        if (!PORTS_OPEN.containsKey(portReference)) {
            System.out.println("Opening new JSSCPort: " + portReference);
            PORTS_OPEN.put(portReference, new JSSCPort(portReference, serialParams));
        } else {
            System.out.println("Fetching previously used JSSCPort: " + portReference);
        }
        return PORTS_OPEN.get(portReference);
    }

    private synchronized void ensurePortOpen() {
        System.out.println("EnsurePortOpen");
        if (closed) {
            try {
                System.out.println("JSSCPort opening");
                serialPort.openPort();
                closed = false;
                serialPort.setParams(
                        serialParams.getBaudRate(),
                        serialParams.getDataBits(),
                        serialParams.getStopBits(),
                        serialParams.getParity()
                );
                System.out.println("JSSCPort opened");
                serialPort.addEventListener(this);
                System.out.println("EnsurePortOpen:Sleep");

                Thread.sleep(1000);
                //clear();
            } catch (SerialPortException | InterruptedException e) {
                throw new SerialResourceException("Could not configure port " + serialPort.getPortName(), e);
            }
        }
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public SerialParams getSerialParams() {
        return serialParams;
    }

    @Override
    public void close() throws IOException {
        try {
            System.out.println("JSSCPort::close");
            serialPort.closePort();
            closed = true;
        } catch (SerialPortException e) {
            throw new IOException("Couldn't close port: ", e);
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public String toString() {
        return String.valueOf(portReference);
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        // TODO : timeouts, etc for disconnect
    }
}
