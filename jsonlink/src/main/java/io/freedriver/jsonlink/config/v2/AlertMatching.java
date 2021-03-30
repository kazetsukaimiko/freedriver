package io.freedriver.jsonlink.config.v2;

import java.util.stream.Stream;

public enum AlertMatching {
    ANY {
        @Override
        public boolean test(double benchmark, AnalogAlertCondition condition, Stream<Double> actual) {
            return actual.anyMatch(sample -> condition.test(benchmark, sample));
        }
    },
    ALL {
        @Override
        public boolean test(double benchmark, AnalogAlertCondition condition, Stream<Double> actual) {
            return actual.allMatch(sample -> condition.test(benchmark, sample));
        }
    },
    NONE {
        @Override
        public boolean test(double benchmark, AnalogAlertCondition condition, Stream<Double> actual) {
            return actual.noneMatch(sample -> condition.test(benchmark, sample));
        }
    };


    public abstract boolean test(double benchmark, AnalogAlertCondition condition, Stream<Double> actual);
}
