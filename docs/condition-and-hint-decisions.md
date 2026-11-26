# Condition and Hint decisions

Read from kazetsukaimiko/freedriver `origin/master` `6fb11d409945a82468b1c56fa089928848a5914f`. Autonomy was read at kazetsukaimiko/autonomy `origin/master` `270c6530a1bfe2520ced881a2dd33fac69683e36`.

This document decides each type below. The Project Manager files the follow-up cards from the last section. This pull request changes only this file.

## Decisions

| Type | Decision |
| --- | --- |
| `io.freedriver.jsonlink.config.Condition` | Removed, with `ConditionType`, `Conditions`, and `Operator` |
| `io.freedriver.base.util.notify.notifysend.hint.Hint` | Removed, with `ByteHint`, `DoubleHint`, `IntHint`, `StringHint`, and `hint.Type` |
| `ByteHint`, `DoubleHint`, `IntHint`, `StringHint` | Removed with `Hint`. Each one only exists to override `getType()` |

`io.freedriver.jsonlink.config.v2.AnalogAlertCondition` is a different type. This decision leaves it, `AlertMatching`, and `AnalogAlert` as they are. The value-class decisions in kazetsukaimiko/freedriver#44 stay in that issue's document. This document leaves those classes undecided.

## Condition on origin/master

`Condition` is an abstract class. Its only state is `private final ConditionType conditionType`, set by a protected constructor, with no accessor. Jackson binds it with `@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "@type")` and no `@JsonSubTypes`. `ConditionType` contains only `BIND`. The reactor has no subclass of `Condition`. The repository has no saved JSON fixture of a `Condition`.

The only holder is `Conditions.nodes`. `Conditions` is already a record with `@Builder(toBuilder = true)`, and its compact constructor copies `children` and `nodes`. On origin/master, `Operator` is referenced only by `Conditions`. No other reactor type references `Condition` or `Conditions`. `JsonLinkModule` registers neither: it registers an `Identifier` key deserializer and serializers for `Mode` and `DigitalState`.

A record decision is unavailable while `Condition` stays abstract. A sealed interface whose implementations are records would need a permitted record for `BIND`. No such record exists, and naming one would invent a payload. That missing `BIND` type is not an open question here, because the decision is removal rather than a new binding. The in-repo sealed pattern, `DevicePathSupplier` (`Id.DEDUCTION`, `@JsonSubTypes`, permits `DeviceByPath` and `DeviceByVendorAndDeviceId`), is a different type. `DeviceByPath` is a record and has no `@Builder`. A Jackson-bound `@Builder` type in this repo is paired with `@Jacksonized`, as `Mappings` is. A removal adds no resolver and no wire format, so it adds no autonomy mappings migration.

Leaving `Condition` as it is would keep a public abstract type Jackson cannot instantiate: `Id.NAME` with no subtype allowlist and no `defaultImpl`. Nothing in the reactor loads `Conditions`. Removal drops that unbindable surface in one card.

A kept or converted `Condition` would keep `ConditionType` and a closed Jackson type id: `Id.NAME` with an explicit subtype allowlist, `DevicePathSupplier`'s `Id.DEDUCTION` with `@JsonSubTypes`, or a `TypeIdResolver` that maps logical names and never calls `Class.forName`. This decision takes removal instead, so the follow-up adds none of those.

jsonlink has no `module-info.java`. The follow-up stays on the classpath jar and adds no jsonlink module descriptor.

## Hint on origin/master

`Hint<T>` is the abstract parent. `ByteHint`, `DoubleHint`, `IntHint`, and `StringHint` are the only subclasses. Each overrides `getType()` to `Type.BYTE`, `Type.DOUBLE`, `Type.INT`, or `Type.STRING`. `Type.toString()` is the enum name lowercased. `Hint.toString()` joins that string, the name, and the value with colons (`type:name:value`). `NotifySend.getHintFlag` passes that string to `notify-send` as `-h`, and an absent hint (`getHint()` null) contributes an empty stream.

`name` is `private final`. `value` is private, is not final, is assigned only in the constructor, and has no setter. `getName()` and `getValue()` are public. Every hint constructor is protected. No class in the reactor constructs a hint subclass. `NotifySend` holds one mutable `Hint` field with `getHint` and `setHint`. `NotifySend.spawn` (line 147) builds its arguments with `Stream.of(getUrgencyFlag(), getExpiryFlag(), getAppNameFlag(), getCategoryFlag(), getHintFlag())` and starts `notify-send`.

Module `io.freedriver.base` exports only `io.freedriver.base.cli`, `io.freedriver.base.util`, and `io.freedriver.base.util.accumulator`. It exports neither `io.freedriver.base.util.notify` nor `io.freedriver.base.util.notify.notifysend.hint`. Follow-up cards leave `module-info.java` unchanged and export neither package.

`TempFileTest`'s `NotifySend` call (lines 22–28) sits inside a block comment (lines 4–29). No live test constructs `NotifySend` or a hint subclass. Follow-up cards leave that test commented out.

A record decision is unavailable for the four subclasses while they extend `Hint`. A sealed interface of four hint records is the worse of the two record shapes: `hint.Type` already is the byte, double, int, and string tag, and four records would repeat it. One immutable record whose discriminator is `hint.Type`, with the four subclasses removed, is the better record shape. That card would change the whole hierarchy at once, compile on its own under `mvn verify`, and keep `NotifySend`'s mutable `Hint` field. It would check the `-h` text once per `hint.Type` (`BYTE`, `DOUBLE`, `INT`, `STRING`) and once for no hint. Those checks, and a check that a present value produces `-h`, would assert on one package-private `NotifySend` method that returns the argument list without starting a process. That method would not count as converting `NotifySend`. The record's canonical constructor would reject a value whose class does not match its `Type` (`Byte`, `Double`, `Integer`, `String`). A hand-written `toString` would keep `type:name:value`, because a generated record `toString` is a different string. A follow-up that made that constructor public would be a visibility widening, would leave both packages unexported, and would reject a colon in the name or the value before it went into the `-h` argument. This design rejects that record anyway: nothing constructs a hint, so the rewrite would keep an unused `NotifySend` field. Removal deletes the unused flag with the types. Unless `Hint` is removed, `NotifySend` keeps that mutable field and every card that keeps a hint keeps and tests the exact `-h` text. This decision removes `Hint`, so the follow-up below is the removal card.

## Autonomy and release

Autonomy at `270c6530a1bfe2520ced881a2dd33fac69683e36` imports jsonlink types other than these (`config.v2.Mappings`, `Mapping`, `MappingsAction`, `Appliance`, `ConnectorConfig`, `DevicePathSupplier`, and the v1 schema types). Its `io.freedriver.autonomy.entity.Conditions` is an empty class in another package. That commit imports none of `Condition`, `Conditions`, `Hint`, `ByteHint`, `DoubleHint`, `IntHint`, `StringHint`, or `NotifySend`.

Condition removal changes public types on the classpath jar `.github/release/publish_jsonlink.sh` deploys. Autonomy still compiles without a source change, because it does not name those types, and no autonomy pull request is required before the freedriver follow-up merges. Hint removal changes public classes in packages `io.freedriver.base` does not export, so the commit is non-breaking. Autonomy still compiles without a source change for the same reason.

This docs commit is `docs(docs)`. release-please leaves the version unchanged for a `docs` commit. `.github/release/publish_jsonlink.sh` stays unchanged and runs only on a `MAJOR.MINOR.PATCH` tag. A later base or jsonlink Java change reaches GitHub Packages only on a tag release-please creates (for a `feat`, a `fix`, or a breaking change). That tag deploys jsonlink plus the reactor modules its POM needs (`mvn -pl jsonlink -am`). jsonlink depends on `serial-impl`, and `serial-impl` depends on `base`, so base is in that set. kazetsukaimiko/freedriver#48 is the existing card for publishing the parent POM, the three BOMs, math-jpa, victron, and victron-jpa. These follow-ups do not wait on #48 for autonomy to keep building.

## Follow-up cards

Two cards. Each branches from master, merges on its own, and keeps the reactor green under `mvn verify`. They share no files. Autonomy keeps building with no autonomy change for these types. This document does not open the cards.

### 1. Remove Condition, ConditionType, Conditions, and Operator

Delete `Condition`, `ConditionType`, `Conditions`, and `Operator` together. Add no `TypeIdResolver` and no subtype allowlist. Leave `AnalogAlertCondition` in place. Add no jsonlink `module-info.java`. Add no autonomy mappings migration.

Commit form, the pair commitlint accepts:

```
refactor(jsonlink)!: remove Condition, ConditionType, Conditions, and Operator

BREAKING CHANGE: delete public jsonlink types Condition, ConditionType, Conditions, and Operator.

Refs: #<card>
```

release-please ranks a `!` after the scope, or a `BREAKING CHANGE:` footer, as a major bump. The pair is required because these types are public on the classpath jar `publish_jsonlink.sh` deploys. A plain `refactor` header is not that mark.

### 2. Remove the notify-send Hint types

Delete `Hint`, `ByteHint`, `DoubleHint`, `IntHint`, `StringHint`, and `hint.Type` together with `NotifySend.hint`, `getHint`, `setHint`, and `getHintFlag`, including the `getHintFlag()` call in `NotifySend.spawn`'s `Stream.of` (line 147 on origin/master). Leave `module-info.java` unchanged. Leave `TempFileTest`'s commented `NotifySend` call commented out.

The card may add one package-private `NotifySend` method that returns the argument list without starting a process. That method is the seam for the test and is not a conversion of `NotifySend`. The one test reads that list and finds no `-h`. It does not call `NotifySend.display` or `ProcessBuilder.start`.

Commit form: `refactor(base): remove the notify-send hint types`, with `Refs: #<card>` and no `!`. The commit is non-breaking because `io.freedriver.base` exports neither the notify package nor the hint package.

Either card can merge first.
