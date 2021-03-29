package io.freedriver.jsonlink.config.v2;

import java.util.stream.Stream;

public enum AlertMatching {
    ANY {
        @Override
        public boolean test(float benchmark, AnalogAlertCondition condition, Stream<Float> actual) {
            return actual.anyMatch(sample -> condition.test(benchmark, sample));
        }
    },
    ALL {
        @Override
        public boolean test(float benchmark, AnalogAlertCondition condition, Stream<Float> actual) {
            return actual.allMatch(sample -> condition.test(benchmark, sample));
        }
    },
    NONE {
        @Override
        public boolean test(float benchmark, AnalogAlertCondition condition, Stream<Float> actual) {
            return actual.noneMatch(sample -> condition.test(benchmark, sample));
        }
    };


    public abstract boolean test(float benchmark, AnalogAlertCondition condition, Stream<Float> actual);
}
