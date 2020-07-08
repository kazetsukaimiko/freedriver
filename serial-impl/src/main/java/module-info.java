module io.freedriver.serial {
    requires jssc;
    requires java.logging;
    //requires usb.api;
    requires io.freedriver.base;
    requires io.freedriver.serial.api;
    //requires io.freedriver.serial.natives;

    //exports io.freedriver.serial;
    //exports io.freedriver.serial.params;
    exports io.freedriver.serial.discovery;
    exports io.freedriver.serial;
}