package io.freedriver.victron;

import io.freedriver.math.measurement.types.electrical.Current;
import io.freedriver.math.measurement.types.electrical.Energy;
import io.freedriver.math.measurement.types.electrical.Potential;
import io.freedriver.math.measurement.types.electrical.Power;
import io.freedriver.victron.vedirect.OffReason;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class VEDirectMessage {
    private Instant timestamp = Instant.now();
    private VictronProduct productType;
    private RelayState relayState;
    private FirmwareVersion firmwareVersion;
    private String serialNumber;
    private Potential mainVoltage;
    private Current mainCurrent;
    private Potential panelVoltage;
    private Power panelPower;
    private StateOfOperation stateOfOperation;
    private TrackerOperation trackerOperation;
    private LoadOutputState loadOutputState;
    private ErrorCode errorCode;
    private OffReason offReason;
    private Energy resettableYield;
    private Energy yieldToday;
    private Power maxPowerToday;
    private Energy yieldYesterday;
    private Power maxPowerYesterday;

    public VEDirectMessage() {
    }

    public VEDirectMessage(VEDirectMessage veDirectMessage) {
        this.timestamp = veDirectMessage.timestamp;
        this.productType = veDirectMessage.productType;
        this.relayState = veDirectMessage.relayState;
        this.firmwareVersion = veDirectMessage.firmwareVersion;
        this.serialNumber = veDirectMessage.serialNumber;
        this.mainVoltage = veDirectMessage.mainVoltage;
        this.mainCurrent = veDirectMessage.mainCurrent;
        this.panelVoltage = veDirectMessage.panelVoltage;
        this.panelPower = veDirectMessage.panelPower;
        this.stateOfOperation = veDirectMessage.stateOfOperation;
        this.trackerOperation = veDirectMessage.trackerOperation;
        this.loadOutputState = veDirectMessage.loadOutputState;
        this.errorCode = veDirectMessage.errorCode;
        this.offReason = veDirectMessage.offReason;
        this.resettableYield = veDirectMessage.resettableYield;
        this.yieldToday = veDirectMessage.yieldToday;
        this.maxPowerToday = veDirectMessage.maxPowerToday;
        this.yieldYesterday = veDirectMessage.yieldYesterday;
        this.maxPowerYesterday = veDirectMessage.maxPowerYesterday;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public VictronProduct getProductType() {
        return productType;
    }

    public void setProductType(VictronProduct productType) {
        this.productType = productType;
    }

    public RelayState getRelayState() {
        return relayState;
    }

    public void setRelayState(RelayState relayState) {
        this.relayState = relayState;
    }

    public FirmwareVersion getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(FirmwareVersion firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Potential getMainVoltage() {
        return mainVoltage;
    }

    public void setMainVoltage(Potential mainVoltage) {
        this.mainVoltage = mainVoltage;
    }

    public Current getMainCurrent() {
        return mainCurrent;
    }

    public void setMainCurrent(Current mainCurrent) {
        this.mainCurrent = mainCurrent;
    }

    public Potential getPanelVoltage() {
        return panelVoltage;
    }

    public void setPanelVoltage(Potential panelVoltage) {
        this.panelVoltage = panelVoltage;
    }

    public Power getPanelPower() {
        return panelPower;
    }

    public void setPanelPower(Power panelPower) {
        this.panelPower = panelPower;
    }

    public StateOfOperation getStateOfOperation() {
        return stateOfOperation;
    }

    public void setStateOfOperation(StateOfOperation stateOfOperation) {
        this.stateOfOperation = stateOfOperation;
    }

    public TrackerOperation getTrackerOperation() {
        return trackerOperation;
    }

    public void setTrackerOperation(TrackerOperation trackerOperation) {
        this.trackerOperation = trackerOperation;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public LoadOutputState getLoadOutputState() {
        return loadOutputState;
    }

    public void setLoadOutputState(LoadOutputState loadOutputState) {
        this.loadOutputState = loadOutputState;
    }

    public OffReason getOffReason() {
        return offReason;
    }

    public void setOffReason(OffReason offReason) {
        this.offReason = offReason;
    }

    public Energy getResettableYield() {
        return resettableYield;
    }

    public void setResettableYield(Energy resettableYield) {
        this.resettableYield = resettableYield;
    }

    public Energy getYieldToday() {
        return yieldToday;
    }

    public void setYieldToday(Energy yieldToday) {
        this.yieldToday = yieldToday;
    }

    public Power getMaxPowerToday() {
        return maxPowerToday;
    }

    public void setMaxPowerToday(Power maxPowerToday) {
        this.maxPowerToday = maxPowerToday;
    }

    public Energy getYieldYesterday() {
        return yieldYesterday;
    }

    public void setYieldYesterday(Energy yieldYesterday) {
        this.yieldYesterday = yieldYesterday;
    }

    public Power getMaxPowerYesterday() {
        return maxPowerYesterday;
    }

    public void setMaxPowerYesterday(Power maxPowerYesterday) {
        this.maxPowerYesterday = maxPowerYesterday;
    }


    @Override
    public String toString() {
        return "VEDirectMessage{" +
                "timestamp=" + timestamp +
                ", product=" + productType +
                ", relayState=" + relayState +
                ", firmwareVersion=" + firmwareVersion +
                ", serialNumber='" + serialNumber + '\'' +
                ", mainVoltage=" + mainVoltage +
                ", mainCurrent=" + mainCurrent +
                ", panelVoltage=" + panelVoltage +
                ", panelPower=" + panelPower +
                ", stateOfOperation=" + stateOfOperation +
                ", trackerOperation=" + trackerOperation +
                ", loadOutputState=" + loadOutputState +
                ", errorCode=" + errorCode +
                ", offReason=" + offReason +
                ", resettableYield=" + resettableYield +
                ", yieldToday=" + yieldToday +
                ", maxPowerToday=" + maxPowerToday +
                ", yieldYesterday=" + yieldYesterday +
                ", maxPowerYesterday=" + maxPowerYesterday +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VEDirectMessage message = (VEDirectMessage) o;
        return Objects.equals(timestamp, message.timestamp) &&
                productType == message.productType &&
                relayState == message.relayState &&
                Objects.equals(firmwareVersion, message.firmwareVersion) &&
                Objects.equals(serialNumber, message.serialNumber) &&
                Objects.equals(mainVoltage, message.mainVoltage) &&
                Objects.equals(mainCurrent, message.mainCurrent) &&
                Objects.equals(panelVoltage, message.panelVoltage) &&
                Objects.equals(panelPower, message.panelPower) &&
                stateOfOperation == message.stateOfOperation &&
                trackerOperation == message.trackerOperation &&
                loadOutputState == message.loadOutputState &&
                errorCode == message.errorCode &&
                offReason == message.offReason &&
                Objects.equals(resettableYield, message.resettableYield) &&
                Objects.equals(yieldToday, message.yieldToday) &&
                Objects.equals(maxPowerToday, message.maxPowerToday) &&
                Objects.equals(yieldYesterday, message.yieldYesterday) &&
                Objects.equals(maxPowerYesterday, message.maxPowerYesterday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, productType, relayState, firmwareVersion, serialNumber, mainVoltage, mainCurrent, panelVoltage, panelPower, stateOfOperation, trackerOperation, loadOutputState, errorCode, offReason, resettableYield, yieldToday, maxPowerToday, yieldYesterday, maxPowerYesterday);
    }
}
