package io.freedriver.jsonlink.config.v2;

public enum AnalogAlertCondition {
    LESS_THAN {
        @Override
        public boolean test(double benchmark, double actual) {
            return actual < benchmark;
        }
    }, // Percent
    MORE_THAN {
        @Override
        public boolean test(double benchmark, double actual) {
            return actual > benchmark;
        }
    }; // Percent

    public abstract boolean test(double benchmark, double actual);
}
