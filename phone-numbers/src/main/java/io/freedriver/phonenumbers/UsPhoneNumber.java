package io.freedriver.phonenumbers;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A US number in E.164 form: {@code +1} and ten ASCII digits.
 *
 * <p>{@link #parse(String)} accepts only that shape. Any other country code or shape is rejected.
 * {@link #toString()} returns the same E.164 form. {@link #getUsAreaCode()} is the three digits
 * after {@code +1}.
 */
@PhoneNumber.Type("us")
public final class UsPhoneNumber implements PhoneNumber {
    /** {@code +1} and ten digits. The normalized value is this string. */
    private static final Pattern E164 = Pattern.compile("\\+1[0-9]{10}");

    /** Entries into the parsing constructor. A rejected type id does not increment this. */
    static final AtomicInteger constructed = new AtomicInteger();

    private final String e164;

    /**
     * ServiceLoader requires a public no-arg constructor. Discovery uses
     * {@link java.util.ServiceLoader.Provider#type()} and does not call this.
     */
    @JsonIgnore
    public UsPhoneNumber() {
        throw new UnsupportedOperationException("ServiceLoader discovery does not construct a number");
    }

    @JsonCreator
    public UsPhoneNumber(@JsonProperty("number") String number) {
        constructed.incrementAndGet();
        this.e164 = canonical(number);
    }

    /**
     * @param raw the whole value; only {@code +1} and ten digits is accepted
     * @throws IllegalArgumentException when {@code raw} is any other shape, including another
     *     country code
     */
    public static UsPhoneNumber parse(String raw) {
        return new UsPhoneNumber(raw);
    }

    public String getNumber() {
        return e164;
    }

    /** Three digits immediately after {@code +1}. */
    @JsonIgnore
    public String getUsAreaCode() {
        return e164.substring(2, 5);
    }

    /** The E.164 form, {@code +1} and ten digits. */
    @Override
    public String toString() {
        return e164;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof UsPhoneNumber number && e164.equals(number.e164);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(e164);
    }

    private static String canonical(String raw) {
        if (raw == null || !E164.matcher(raw).matches()) {
            throw new IllegalArgumentException("must be a US phone number in +1XXXXXXXXXX form");
        }
        return raw;
    }
}
