package jssc;

import io.freedriver.base.util.ClasspathUtil;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class SerialNativeInterfaceTest {


    @Test
    public void testInitNativeInterface() {
        ClasspathUtil.getResources(".*\\.so", ".*\\.h")
                .forEach(System.out::println);

        SerialNativeInterface serial = new SerialNativeInterface();

        long handle = -1;
        try {
            handle = serial.openPort("ttyS0",false);
            assertThat(handle, is(not(-1L)));
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
            assertThat(nativeLibraryVersion, is(not(nullValue())));
            assertThat(nativeLibraryVersion, is(not("")));
        } catch (UnsatisfiedLinkError linkError) {

            ClasspathUtil.getResources(".*\\.so", ".*\\.h")
                    .forEach(System.out::println);
            linkError.printStackTrace();
            fail("Should be able to call method!");
        }

    }

}
