package io.freedriver.ipb2wp;

import io.freedriver.jpa.model.wordpress.WpUser;
import lombok.NonNull;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import io.freedriver.clients.ipb.model.core.Member;

public enum WpUsersMapper implements BiFunction<Member, WpUser.WpUserBuilder, WpUser.WpUserBuilder> {
    login(Member::getName, WpUser.WpUserBuilder::login),
    pass("$P$BxYeKKMNSxr49M0tyqef0.SgCbNJAd/", WpUser.WpUserBuilder::pass),
    niceName(Member::getFormattedName, WpUser.WpUserBuilder::niceName),
    displayName(Member::getFormattedName, WpUser.WpUserBuilder::displayName),

    email(Member::getEmail, WpUser.WpUserBuilder::email),

    registered(Member::getJoined, WpUser.WpUserBuilder::registered),
    activationKey("", WpUser.WpUserBuilder::activationKey),
    url("", WpUser.WpUserBuilder::url),
    status((m, b) -> b.status(0))
    ;

    private final BiFunction<Member, WpUser.WpUserBuilder, WpUser.WpUserBuilder> mapper;

    WpUsersMapper(@NonNull BiFunction<Member, WpUser.WpUserBuilder, WpUser.WpUserBuilder> mapper) {
        this.mapper = mapper;
    }

    <T> WpUsersMapper(@NonNull Function<Member, T> getter, @NonNull BiFunction<WpUser.WpUserBuilder, T, WpUser.WpUserBuilder> setter) {
        this((member, builder) -> setter.apply(builder, getter.apply(member)));
    }


    WpUsersMapper(@NonNull String fixedValue, @NonNull BiFunction<WpUser.WpUserBuilder, String, WpUser.WpUserBuilder> setter) {
        this((member, builder) -> setter.apply(builder, fixedValue));
    }

    public static Stream<WpUsersMapper> stream() {
        return Stream.of(values());
    }

    public static WpUser create(Member member) {
        WpUser.WpUserBuilder builder = WpUser.builder();
        WpUsersMapper.stream()
                .forEach(mapper -> mapper.apply(member, builder));
        return builder.build();
    }

    @Override
    public WpUser.WpUserBuilder apply(Member member, WpUser.WpUserBuilder WpUserBuilder) {
        return mapper.apply(member, WpUserBuilder);
    }
}

/*
        WpUsers penguin = WpUsers.builder()
                .login("penguin")
                .pass("$P$BxYeKKMNSxr49M0tyqef0.SgCbNJAd/")
                .niceName("penguin")
                .email("penguin@genetrysolar.com")
                .registered(LocalDateTime.now())
                .activationKey("")
                .url("")
                .status(0)
                .displayName("Penguin")
                .build();

 */
