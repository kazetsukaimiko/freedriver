package io.freedriver.base.util.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ThreadLocalRandom;

public class NetUtils {

    private static final int PORT_UPPER = 65535;
    private static final int PORT_LOWER = 49152;

    public static int randomAvailablePort() {
        return nextFreePort(PORT_LOWER, PORT_UPPER);
    }

    private static int nextFreePort(int from, int to) {
        int port = PORT_LOWER;
        while (true) {
            if (isLocalPortFree(port)) {
                return port;
            } else {
                port = ThreadLocalRandom.current().nextInt(from, to);
            }
        }
    }

    private static boolean isLocalPortFree(int port) {
        try {
            new ServerSocket(port).close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
