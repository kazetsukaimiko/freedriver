module victron.java {
    requires java.logging;
    requires io.freedriver.math;
    requires io.freedriver.serial;
    requires io.freedriver.base;
    exports io.freedriver.victron;
    exports io.freedriver.victron.vedirect;
}