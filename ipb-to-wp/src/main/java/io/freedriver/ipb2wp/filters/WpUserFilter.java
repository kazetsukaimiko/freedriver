package io.freedriver.ipb2wp.filters;

import io.freedriver.jpa.model.wordpress.WpUser;
import lombok.extern.slf4j.Slf4j;

import java.text.Normalizer;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j
public enum WpUserFilter implements Function<WpUser, WpUser>, Predicate<WpUser> {
    user_login {
        // Replace accented characters, replace series of spaces with single underscore, and remove comma, period.
        // Copies the object.
        @Override
        public WpUser apply(WpUser wpUser) {
            return wpUser.withLogin(Normalizer.normalize(wpUser.getLogin(), Normalizer.Form.NFD)
                    .replaceAll("\\s+", "_")
                    .replaceAll(",", "")
                    .replaceAll("\\.", ""));
        }

        // Only apply if the user login contains any commas or accented characters.
        @Override
        public boolean test(WpUser wpUser) {
            String s = wpUser.getLogin();
            return !Objects.equals(Normalizer.normalize(s, Normalizer.Form.NFD), s)
                    || s.contains("[\\s,\\.]+");
        }
    }
    ;

    public static Stream<WpUserFilter> stream() {
        return Stream.of(values());
    }
    public static boolean requiresCorrection(WpUser wpUser) {
        return stream()
                .anyMatch(filter -> filter.test(wpUser));
    }

    public static WpUser correctAll(WpUser wpUser) {
        return stream()
                .filter(filter -> filter.test(wpUser))
                .peek(filter -> log.info("Correcting invalid {} for {}", filter.name(), wpUser))
                .reduce(wpUser, (user, filter) -> filter.apply(user), (a, b) -> b);
    }
}
