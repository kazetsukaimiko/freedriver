module io.freedriver.math {
    requires java.logging;
    requires static lombok;
    exports io.freedriver.math;
    exports io.freedriver.math.number;
    exports io.freedriver.math.measurement.units;
    exports io.freedriver.math.measurement.types;
    exports io.freedriver.math.measurement.types.electrical;
    exports io.freedriver.math.measurement.types.thermo;
}