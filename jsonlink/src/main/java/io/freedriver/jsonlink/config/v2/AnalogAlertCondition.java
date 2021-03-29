package io.freedriver.jsonlink.config.v2;

public enum AnalogAlertCondition {
    LESS_THAN {
        @Override
        public boolean test(float benchmark, float actual) {
            return actual < benchmark;
        }
    }, // Percent
    MORE_THAN {
        @Override
        public boolean test(float benchmark, float actual) {
            return actual > benchmark;
        }
    }; // Percent

    public abstract boolean test(float benchmark, float actual);
}
