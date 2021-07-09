module victron.java {
    requires java.logging;
    requires io.freedriver.math;
    requires io.freedriver.serial.impl;
    requires io.freedriver.base;
    requires io.freedriver.serial.api;
    //exports io.freedriver.daly;
    exports io.freedriver.daly.bms;
    exports io.freedriver.daly.bms.checksum;
    exports io.freedriver.daly.bms.checksum.debug;
    exports io.freedriver.daly.bms.exception;
    exports io.freedriver.daly.bms.stream;
}