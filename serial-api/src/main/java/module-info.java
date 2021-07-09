module io.freedriver.serial.api {
    requires java.logging;
    requires io.freedriver.math;
    requires io.freedriver.base;
    exports io.freedriver.serial.api;
    exports io.freedriver.serial.api.params;
    exports io.freedriver.serial.api.exception;
    exports io.freedriver.serial.stream.api;
}