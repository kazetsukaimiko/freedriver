package io.freedriver.generty.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Statsjson {
    private SetupSection setup;
    private OutputsSection outputs;
    private InputsSection inputs;
    private StatsSection stats;
    private TempsSection temps;
    private FansSection fans;
    private ErrorsSection errors;

    public SetupSection getSetup() {
        return setup;
    }

    public void setSetup(SetupSection setup) {
        this.setup = setup;
    }

    public OutputsSection getOutputs() {
        return outputs;
    }

    public void setOutputs(OutputsSection outputs) {
        this.outputs = outputs;
    }

    public InputsSection getInputs() {
        return inputs;
    }

    public void setInputs(InputsSection inputs) {
        this.inputs = inputs;
    }

    public StatsSection getStats() {
        return stats;
    }

    public void setStats(StatsSection stats) {
        this.stats = stats;
    }

    public TempsSection getTemps() {
        return temps;
    }

    public void setTemps(TempsSection temps) {
        this.temps = temps;
    }

    public FansSection getFans() {
        return fans;
    }

    public void setFans(FansSection fans) {
        this.fans = fans;
    }

    public ErrorsSection getErrors() {
        return errors;
    }

    public void setErrors(ErrorsSection errors) {
        this.errors = errors;
    }


}
