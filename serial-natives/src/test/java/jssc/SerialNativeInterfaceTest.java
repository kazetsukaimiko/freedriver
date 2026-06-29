package jssc;

import io.freedriver.base.util.ClasspathUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SerialNativeInterfaceTest {


    @Test
    public void testInitNativeInterface() {
        ClasspathUtil.getResources(".*\\.so", ".*\\.h")
                .forEach(System.out::println);

        SerialNativeInterface serial = new SerialNativeInterface();

        long handle = -1;
        try {
            handle = serial.openPort("ttyS0",false);
            assertNotEquals(-1L, handle);
        } finally {
            if (handle != -1) {
                serial.closePort(handle);
            }
        }
    }

    @Test
    public void testPrintVersion() {
        try {
            final String nativeLibraryVersion = SerialNativeInterface.getNativeLibraryVersion();
            assertNotNull(nativeLibraryVersion);
            assertNotEquals("", nativeLibraryVersion);
        } catch (UnsatisfiedLinkError linkError) {

            ClasspathUtil.getResources(".*\\.so", ".*\\.h")
                    .forEach(System.out::println);
            linkError.printStackTrace();
            fail("Should be able to call method!");
        }

    }

}
