package io.freedriver.victron;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirmwareVersion {
    private static final Pattern FW_PATTERN = Pattern.compile("(?<candidate>[a-zA-Z]+)?(?<version>\\d{3})");
    private static final Pattern FWE_PATTERN = Pattern.compile("0?(?<version>\\d{1,3})(?<type>[\\d[a-zA-Z]]{2})");
    private static final Pattern DB_PATTERN = Pattern.compile("v(?<version>\\d+\\.\\d\\d+)(-(?<todorest>.*))?");

    private BigDecimal version;

    private String representation;
    private String candidate;
    private String beta;
    private boolean release;

    public FirmwareVersion() {
    }

    public FirmwareVersion(String representation) {
        Matcher fwMatcher = FW_PATTERN.matcher(representation);
        Matcher fweMatcher = FWE_PATTERN.matcher(representation);
        Matcher dbMatcher = DB_PATTERN.matcher(representation);
        this.representation = representation;
        if (fwMatcher.matches()) {
            this.version = new BigDecimal(fwMatcher.group("version")).movePointLeft(2);
            this.candidate = fwMatcher.group("candidate");
            this.release = (this.candidate == null);
            this.beta = null;
        } else if (fweMatcher.matches()) {
            this.candidate = null;
            this.version = new BigDecimal(fweMatcher.group("version")).movePointLeft(2);
            String type = fweMatcher.group("type");
            if (Objects.equals("FF", type)) {
                this.release = true;
                this.beta = null;
            } else {
                this.release = false;
                this.beta = type;
            }
        } else if (dbMatcher.matches()) {
            this.version = new BigDecimal(dbMatcher.group("version"));
            // TODO
            this.release = true;
            this.candidate = null;
            this.beta = null;
        } else {
            throw new IllegalArgumentException("Bad or unsupported Firmware Version: " + representation);
        }
    }

    public BigDecimal getVersion() {
        return version;
    }

    public void setVersion(BigDecimal version) {
        this.version = version;
    }

    public String getRepresentation() {
        return representation;
    }

    public void setRepresentation(String representation) {
        this.representation = representation;
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public String getBeta() {
        return beta;
    }

    public void setBeta(String beta) {
        this.beta = beta;
    }

    public boolean isRelease() {
        return release;
    }

    public void setRelease(boolean release) {
        this.release = release;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FirmwareVersion that = (FirmwareVersion) o;
        return release == that.release &&
                Objects.equals(version, that.version) &&
                Objects.equals(representation, that.representation) &&
                Objects.equals(candidate, that.candidate) &&
                Objects.equals(beta, that.beta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, representation, candidate, beta, release);
    }

    @Override
    public String toString() {
        return "v" + getVersion().toPlainString()
                + ((isRelease()) ? "" :
                    getBeta() == null ?
                        "-rc-" + getCandidate()
                            :
                        "-beta-" + getBeta());
    }
}
