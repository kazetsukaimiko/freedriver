package io.freedriver.victron;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Builder;

@Builder(toBuilder = true)
public record FirmwareVersion(
        BigDecimal version,
        String representation,
        String candidate,
        String beta,
        boolean release) {

    private static final Pattern FW_PATTERN = Pattern.compile("(?<candidate>[a-zA-Z]+)?(?<version>\\d{3})");
    private static final Pattern FWE_PATTERN = Pattern.compile("0?(?<version>\\d{1,3})(?<type>[\\d[a-zA-Z]]{2})");
    private static final Pattern DB_PATTERN = Pattern.compile("v(?<version>\\d+\\.\\d\\d+)(-(?<todorest>.*))?");

    public FirmwareVersion(String representation) {
        this(parse(representation));
    }

    private FirmwareVersion(Parsed parsed) {
        this(parsed.version(), parsed.representation(), parsed.candidate(), parsed.beta(), parsed.release());
    }

    private static Parsed parse(String representation) {
        Matcher fwMatcher = FW_PATTERN.matcher(representation);
        Matcher fweMatcher = FWE_PATTERN.matcher(representation);
        Matcher dbMatcher = DB_PATTERN.matcher(representation);
        if (fwMatcher.matches()) {
            String candidate = fwMatcher.group("candidate");
            return new Parsed(
                    new BigDecimal(fwMatcher.group("version")).movePointLeft(2),
                    representation,
                    candidate,
                    null,
                    candidate == null);
        }
        if (fweMatcher.matches()) {
            String type = fweMatcher.group("type");
            boolean release = "FF".equals(type);
            return new Parsed(
                    new BigDecimal(fweMatcher.group("version")).movePointLeft(2),
                    representation,
                    null,
                    release ? null : type,
                    release);
        }
        if (dbMatcher.matches()) {
            return new Parsed(
                    new BigDecimal(dbMatcher.group("version")),
                    representation,
                    null,
                    null,
                    true);
        }
        throw new IllegalArgumentException("Bad or unsupported Firmware Version: " + representation);
    }

    @Override
    public String toString() {
        return "v" + version().toPlainString()
                + (release() ? "" :
                    beta() == null ?
                        "-rc-" + candidate()
                            :
                        "-beta-" + beta());
    }

    private record Parsed(BigDecimal version, String representation, String candidate, String beta, boolean release) {}
}
