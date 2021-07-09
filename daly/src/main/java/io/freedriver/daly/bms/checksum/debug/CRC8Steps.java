package io.freedriver.daly.bms.checksum.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Exposes how CRC8 checksums are calculated.
 */
public class CRC8Steps implements CRC8Debugger {
    private List<CRC8Step> steps = new ArrayList<>();
    private int crc = 0;

    public List<CRC8Step> getSteps() {
        if (steps == null) {
            steps = new ArrayList<>();
        }
        return steps;
    }

    public void setSteps(List<CRC8Step> steps) {
        this.steps = steps;
    }

    public int getCrc() {
        return crc;
    }

    public void setCrc(int crc) {
        this.crc = crc;
    }

    public void setCrc(CRC8Step step) {
        setCrc(step.getEnd() & 0xFF);
        getSteps().add(step);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CRC8Steps crc8Steps = (CRC8Steps) o;
        return crc == crc8Steps.crc && Objects.equals(steps, crc8Steps.steps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(steps, crc);
    }

    @Override
    public String toString() {
        return "(["+ steps.stream().map(CRC8Step::toString).collect(Collectors.joining(", "))+"] => " + crc + ")";
    }


    @Override
    public void append(int start, int component, int end) {
        CRC8Step step = new CRC8Step();
        step.setStart(start);
        step.setComponent(component);
        step.setEnd(end);
        setCrc(step);
    }
}
