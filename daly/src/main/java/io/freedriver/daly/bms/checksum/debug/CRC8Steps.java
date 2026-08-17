package io.freedriver.daly.bms.checksum.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Exposes how CRC8 checksums are calculated.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
public class CRC8Steps implements CRC8Debugger {
    @Builder.Default
    private List<CRC8Step> steps = new ArrayList<>();
    private int crc;

    public List<CRC8Step> getSteps() {
        if (steps == null) {
            steps = new ArrayList<>();
        }
        return steps;
    }

    public void addStep(CRC8Step step) {
        this.crc = step.end() & 0xFF;
        getSteps().add(step);
    }

    @Override
    public String toString() {
        return "(["+ steps.stream().map(CRC8Step::toString).collect(Collectors.joining(", "))+"] => " + crc + ")";
    }


    @Override
    public void append(int start, int component, int end) {
        addStep(new CRC8Step(start, component, end));
    }
}
