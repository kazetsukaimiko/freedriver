module victron.java {
    requires java.logging;
    requires io.freedriver.math;
    requires io.freedriver.serial.impl;
    requires io.freedriver.base;
    requires io.freedriver.serial.api;
    exports io.freedriver.victron;
    exports io.freedriver.victron.vedirect;
    exports io.freedriver.victron.hex;
}