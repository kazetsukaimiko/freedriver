module io.freedriver.eletrodacus {
    requires java.logging;
    requires io.freedriver.math;
    requires io.freedriver.serial.api;
    requires io.freedriver.serial.impl;
    exports io.freedriver.electrodacus.sbms;
}