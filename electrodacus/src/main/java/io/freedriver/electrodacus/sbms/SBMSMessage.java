package io.freedriver.electrodacus.sbms;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import io.freedriver.math.measurement.types.electrical.Current;
import io.freedriver.math.measurement.types.electrical.Potential;
import io.freedriver.math.measurement.types.thermo.Temperature;
import lombok.Builder;

@Builder(toBuilder = true)
public record SBMSMessage(
        Path path,
        Instant timestamp,
        double soc,
        Potential cellOne,
        Potential cellTwo,
        Potential cellThree,
        Potential cellFour,
        Potential cellFive,
        Potential cellSix,
        Potential cellSeven,
        Potential cellEight,
        Temperature internalTemperature,
        Temperature externalTemperature,
        boolean charging,
        boolean discharging,
        Current batteryCurrent,
        Current pvCurrent1,
        Current pvCurrent2,
        Current extCurrent,
        Set<ErrorCode> errorCodes) {

    public static Optional<SBMSMessage> of(Path path, byte[] data) {
        if (data.length == 60) {
            SBMSMessage message = SBMSMessage.builder().path(path).build();
            for (SBMSFieldSetter setter : SBMSFieldSetter.values()) {
                message = setter.apply(message, data);
            }
            return Optional.of(message);
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "SBMSMessage{" +
                "timestamp=" + timestamp +
                ", soc=" + soc +
                ", cellOne=" + cellOne +
                ", cellTwo=" + cellTwo +
                ", cellThree=" + cellThree +
                ", cellFour=" + cellFour +
                ", cellFive=" + cellFive +
                ", cellSix=" + cellSix +
                ", cellSeven=" + cellSeven +
                ", cellEight=" + cellEight +
                ", internalTemperature=" + internalTemperature +
                ", externalTemperature=" + externalTemperature +
                ", charging=" + charging +
                ", discharging=" + discharging +
                ", batteryCurrent=" + batteryCurrent +
                ", pvCurrent1=" + pvCurrent1 +
                ", pvCurrent2=" + pvCurrent2 +
                ", extCurrent=" + extCurrent +
                ", errorCodes=" + errorCodes +
                '}';
    }

}
