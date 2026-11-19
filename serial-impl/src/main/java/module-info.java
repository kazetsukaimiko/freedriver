module io.freedriver.serial.impl {
    requires jssc;
    requires java.logging;
    //requires usb.api;
    requires io.freedriver.base;
    requires io.freedriver.serial.api;
    requires static lombok;

    //exports io.freedriver.serial;
    //exports io.freedriver.serial.api.params;
    exports io.freedriver.serial.discovery;
    exports io.freedriver.serial;
    exports io.freedriver.serial.connection;
}