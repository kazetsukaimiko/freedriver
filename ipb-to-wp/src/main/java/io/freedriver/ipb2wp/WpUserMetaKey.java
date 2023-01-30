package io.freedriver.ipb2wp;

import io.freedriver.clients.ipb.model.core.Member;
import io.freedriver.jpa.model.wordpress.WpUserMeta;
import io.freedriver.jpa.model.wordpress.WpUser;
import lombok.NonNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public enum WpUserMetaKey implements Function<Member, String> {
    wp_user_level("0"),
    wp_capabilities("a:1:{s:10:\"subscriber\";b:1;}"),
    nickname(Member::getFormattedName),
    billing_email(Member::getEmail)
    ;

    private final Function<Member, String> mapper;

    WpUserMetaKey(@NonNull String saneDefault) {
        this(m -> saneDefault);
    }
    WpUserMetaKey(@NonNull Function<Member, String> mapper) {
        this.mapper = mapper;
    }

    @Override
    public String apply(Member member) {
        return mapper.apply(member);
    }

    public WpUserMeta create(Member member, WpUser wpUser) {
        WpUserMeta meta = new WpUserMeta();
        Optional.ofNullable(wpUser).map(WpUser::getId).ifPresent(meta::setUserId);
        meta.setKey(name());
        meta.setValue(mapper.apply(member));
        return meta;
    }

    public static Stream<WpUserMetaKey> stream() {
        return Stream.of(values());
    }
}
/*
mysql> SELECT * FROM wp_usermeta WHERE user_id = 5 AND length(meta_value) <= 50;
        +----------+---------+--------------------------------+------------------------------+
        | umeta_id | user_id | meta_key                       | meta_value                   |
        +----------+---------+--------------------------------+------------------------------+
        |      188 |       5 | wc_last_active                 | 1674950400                   |
        |      190 |       5 | _woocommerce_persistent_cart_1 | a:1:{s:4:"cart";a:0:{}}      |
        |      193 |       5 | nickname                       | penguin                      |
        |      194 |       5 | first_name                     | Pen                          |
        |      195 |       5 | last_name                      | Guin                         |
        |      196 |       5 | wp_user_level                  | 0                            |
        |      203 |       5 | _wcs_subscription_ids_cache    | a:0:{}                       |
        |      232 |       5 | wp_capabilities                | a:1:{s:10:"subscriber";b:1;} |
        +----------+---------+--------------------------------+------------------------------+
        8 rows in set (0.00 sec)
 */