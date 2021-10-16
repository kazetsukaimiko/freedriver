package io.freedriver.genetry.wifi;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.freedriver.math.measurement.types.electrical.Current;
import io.freedriver.math.measurement.types.electrical.Potential;
import io.freedriver.math.measurement.types.electrical.Power;
import io.freedriver.math.number.ScaledNumber;

import java.util.Objects;

public class BoardData {
    @JsonProperty("setup")
    Setup setup;
    @JsonProperty("outputs")
    Output output;
    @JsonProperty("inputs")
    Input input;
    @JsonProperty("stats")
    Statistics statistics;
    /*
    {

        "temps": {
                "rdx": "F",
                "T1": 112.6,
                "T2": 104.3,
                "T3": -0.1,
                "T4": -0.1,
                "T5": -0.1,
                "T6": -0.1,
                "TT": 102.3,
                "TM": 100.3,
                "TC": -0.1,
                "TA": 85.4
        },
        "fans": {
                "FA": 20,
                "FB": 20,
                "FC": 20
        },
        "errors": {
                "Alms": "00000000000000000",
                "Err": 0
        }
     }

     */


    /*
            "stats": {
                "invmode": 1,
                "power": 1,
                "hrs": 596,
                "KWh": 296.6,
                "Ver": "1.1r3"
        },
     */

    public enum InverterMode {
        ON,
        CHARGING,
    }

    public static class Statistics {
        //InverterMode
    }

/*
"inputs": {
                "ACin": 236,
                "ChrgA": 0,
                "BattV": 53.4
        },
 */

    public static class Input {
        @JsonProperty("ACin")
        private Potential acInputVoltage;
        @JsonProperty("ChrgA")
        private Current chargeAmperage;
        @JsonProperty("BattV")
        private Potential batteryVoltage;

        public Input() {
        }

        public Potential getAcInputVoltage() {
            return acInputVoltage;
        }

        public void setAcInputVoltage(Potential acInputVoltage) {
            this.acInputVoltage = acInputVoltage;
        }

        public Current getChargeAmperage() {
            return chargeAmperage;
        }

        public void setChargeAmperage(Current chargeAmperage) {
            this.chargeAmperage = chargeAmperage;
        }

        public Potential getBatteryVoltage() {
            return batteryVoltage;
        }

        public void setBatteryVoltage(Potential batteryVoltage) {
            this.batteryVoltage = batteryVoltage;
        }
    }


/*
        "outputs": {
                "outV": 244,
                "outA": 2.4,
                "outW": 566,
                "outPF": 1.19,
                "outHZ": 60
         },
 */

    public static class Output {
        @JsonProperty("outV")
        private Potential outputVoltage;
        @JsonProperty("outA")
        private Current outputCurrent;
        @JsonProperty("outW")
        private Power outputWattage;
        @JsonProperty("outPF")
        private ScaledNumber outputPowerFactor;
        @JsonProperty("outHZ")
        private ScaledNumber outputFrequencyHZ; // TODO Measurement Unit

        public Output() {
        }

        public Potential getOutputVoltage() {
            return outputVoltage;
        }

        public void setOutputVoltage(Potential outputVoltage) {
            this.outputVoltage = outputVoltage;
        }

        public Current getOutputCurrent() {
            return outputCurrent;
        }

        public void setOutputCurrent(Current outputCurrent) {
            this.outputCurrent = outputCurrent;
        }

        public Power getOutputWattage() {
            return outputWattage;
        }

        public void setOutputWattage(Power outputWattage) {
            this.outputWattage = outputWattage;
        }

        public ScaledNumber getOutputPowerFactor() {
            return outputPowerFactor;
        }

        public void setOutputPowerFactor(ScaledNumber outputPowerFactor) {
            this.outputPowerFactor = outputPowerFactor;
        }

        public ScaledNumber getOutputFrequencyHZ() {
            return outputFrequencyHZ;
        }

        public void setOutputFrequencyHZ(ScaledNumber outputFrequencyHZ) {
            this.outputFrequencyHZ = outputFrequencyHZ;
        }
    }

    public static class Setup {
        @JsonProperty("model")
        private String inverterModel;
        @JsonProperty("hardver")
        private int hardwareVersion;
        @JsonProperty("UVPa")
        private Potential underVoltageAlarm;
        @JsonProperty("UVPe")
        private Potential underVoltageError;
        @JsonProperty("OVPa")
        private Potential overVoltageAlarm;
        @JsonProperty("OVPe")
        private Potential overVoltageError;
        @JsonProperty("ChargeIn")
        private Potential chargeInputSetting;

        public Setup() {
        }

        public String getInverterModel() {
            return inverterModel;
        }

        public void setInverterModel(String inverterModel) {
            this.inverterModel = inverterModel;
        }

        public int getHardwareVersion() {
            return hardwareVersion;
        }

        public void setHardwareVersion(int hardwareVersion) {
            this.hardwareVersion = hardwareVersion;
        }

        public Potential getUnderVoltageAlarm() {
            return underVoltageAlarm;
        }

        public void setUnderVoltageAlarm(Potential underVoltageAlarm) {
            this.underVoltageAlarm = underVoltageAlarm;
        }

        public Potential getUnderVoltageError() {
            return underVoltageError;
        }

        public void setUnderVoltageError(Potential underVoltageError) {
            this.underVoltageError = underVoltageError;
        }

        public Potential getOverVoltageAlarm() {
            return overVoltageAlarm;
        }

        public void setOverVoltageAlarm(Potential overVoltageAlarm) {
            this.overVoltageAlarm = overVoltageAlarm;
        }

        public Potential getOverVoltageError() {
            return overVoltageError;
        }

        public void setOverVoltageError(Potential overVoltageError) {
            this.overVoltageError = overVoltageError;
        }

        public Potential getChargeInputSetting() {
            return chargeInputSetting;
        }

        public void setChargeInputSetting(Potential chargeInputSetting) {
            this.chargeInputSetting = chargeInputSetting;
        }


        @Override
        public int hashCode() {
            return Objects.hash(inverterModel, hardwareVersion, chargeInputSetting);
        }
    }
}
