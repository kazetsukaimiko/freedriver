package io.freedriver.electrodacus.sbms;

import io.freedriver.math.measurement.types.electrical.Current;
import io.freedriver.math.measurement.types.electrical.Potential;
import io.freedriver.math.measurement.types.thermo.Temperature;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class SBMSMessage {
    private Path path;
    private Instant timestamp;
    private double soc;
    private Potential cellOne;
    private Potential cellTwo;
    private Potential cellThree;
    private Potential cellFour;
    private Potential cellFive;
    private Potential cellSix;
    private Potential cellSeven;
    private Potential cellEight;

    private Temperature internalTemperature;
    private Temperature externalTemperature;

    private boolean charging;
    private boolean discharging;

    private Current batteryCurrent;
    private Current pvCurrent1;
    private Current pvCurrent2;
    private Current extCurrent;

    // AD2
    // AD3
    // HT1
    // HT2

    private Set<ErrorCode> errorCodes;

    public SBMSMessage() {
    }

    private SBMSMessage(Path path, byte[] data) {
        this.path = path;
        SBMSFieldSetter.stream()
                .forEach(setter -> setter.apply(this, data));
    }

    public static Optional<SBMSMessage> of(Path path, byte[] data) {
        if (data.length == 60) {
            return Optional.of(new SBMSMessage(path, data));
        }
        return Optional.empty();
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public double getSoc() {
        return soc;
    }

    public void setSoc(double soc) {
        this.soc = soc;
    }

    public Potential getCellOne() {
        return cellOne;
    }

    public void setCellOne(Potential cellOne) {
        this.cellOne = cellOne;
    }

    public Potential getCellTwo() {
        return cellTwo;
    }

    public void setCellTwo(Potential cellTwo) {
        this.cellTwo = cellTwo;
    }

    public Potential getCellThree() {
        return cellThree;
    }

    public void setCellThree(Potential cellThree) {
        this.cellThree = cellThree;
    }

    public Potential getCellFour() {
        return cellFour;
    }

    public void setCellFour(Potential cellFour) {
        this.cellFour = cellFour;
    }

    public Potential getCellFive() {
        return cellFive;
    }

    public void setCellFive(Potential cellFive) {
        this.cellFive = cellFive;
    }

    public Potential getCellSix() {
        return cellSix;
    }

    public void setCellSix(Potential cellSix) {
        this.cellSix = cellSix;
    }

    public Potential getCellSeven() {
        return cellSeven;
    }

    public void setCellSeven(Potential cellSeven) {
        this.cellSeven = cellSeven;
    }

    public Potential getCellEight() {
        return cellEight;
    }

    public void setCellEight(Potential cellEight) {
        this.cellEight = cellEight;
    }

    public Temperature getInternalTemperature() {
        return internalTemperature;
    }

    public void setInternalTemperature(Temperature internalTemperature) {
        this.internalTemperature = internalTemperature;
    }

    public Temperature getExternalTemperature() {
        return externalTemperature;
    }

    public void setExternalTemperature(Temperature externalTemperature) {
        this.externalTemperature = externalTemperature;
    }

    public boolean isCharging() {
        return charging;
    }

    public void setCharging(boolean charging) {
        this.charging = charging;
    }

    public boolean isDischarging() {
        return discharging;
    }

    public void setDischarging(boolean discharging) {
        this.discharging = discharging;
    }

    public Current getBatteryCurrent() {
        return batteryCurrent;
    }

    public void setBatteryCurrent(Current batteryCurrent) {
        this.batteryCurrent = batteryCurrent;
    }

    public Current getPvCurrent1() {
        return pvCurrent1;
    }

    public void setPvCurrent1(Current pvCurrent1) {
        this.pvCurrent1 = pvCurrent1;
    }

    public Current getPvCurrent2() {
        return pvCurrent2;
    }

    public void setPvCurrent2(Current pvCurrent2) {
        this.pvCurrent2 = pvCurrent2;
    }

    public Current getExtCurrent() {
        return extCurrent;
    }

    public void setExtCurrent(Current extCurrent) {
        this.extCurrent = extCurrent;
    }

    public Set<ErrorCode> getErrorCodes() {
        return errorCodes;
    }

    public void setErrorCodes(Set<ErrorCode> errorCodes) {
        this.errorCodes = errorCodes;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SBMSMessage that = (SBMSMessage) o;
        return Double.compare(that.soc, soc) == 0 &&
                charging == that.charging &&
                discharging == that.discharging &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(cellOne, that.cellOne) &&
                Objects.equals(cellTwo, that.cellTwo) &&
                Objects.equals(cellThree, that.cellThree) &&
                Objects.equals(cellFour, that.cellFour) &&
                Objects.equals(cellFive, that.cellFive) &&
                Objects.equals(cellSix, that.cellSix) &&
                Objects.equals(cellSeven, that.cellSeven) &&
                Objects.equals(cellEight, that.cellEight) &&
                Objects.equals(internalTemperature, that.internalTemperature) &&
                Objects.equals(externalTemperature, that.externalTemperature) &&
                Objects.equals(batteryCurrent, that.batteryCurrent) &&
                Objects.equals(pvCurrent1, that.pvCurrent1) &&
                Objects.equals(pvCurrent2, that.pvCurrent2) &&
                Objects.equals(extCurrent, that.extCurrent) &&
                Objects.equals(errorCodes, that.errorCodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, soc, cellOne, cellTwo, cellThree, cellFour, cellFive, cellSix, cellSeven, cellEight, internalTemperature, externalTemperature, charging, discharging, batteryCurrent, pvCurrent1, pvCurrent2, extCurrent, errorCodes);
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
