package io.freedriver.victron;

import java.time.Instant;

import io.freedriver.math.measurement.types.electrical.Current;
import io.freedriver.math.measurement.types.electrical.Energy;
import io.freedriver.math.measurement.types.electrical.Potential;
import io.freedriver.math.measurement.types.electrical.Power;
import io.freedriver.victron.vedirect.OffReason;
import lombok.Builder;

@Builder(toBuilder = true)
public record VEDirectMessage(
        Instant timestamp,
        VictronProduct productType,
        RelayState relayState,
        FirmwareVersion firmwareVersion,
        String serialNumber,
        Potential mainVoltage,
        Current mainCurrent,
        Potential panelVoltage,
        Power panelPower,
        StateOfOperation stateOfOperation,
        TrackerOperation trackerOperation,
        LoadOutputState loadOutputState,
        ErrorCode errorCode,
        OffReason offReason,
        Energy resettableYield,
        Energy yieldToday,
        Power maxPowerToday,
        Energy yieldYesterday,
        Power maxPowerYesterday) {

    public VEDirectMessage {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }

    public VEDirectMessage() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }
}
