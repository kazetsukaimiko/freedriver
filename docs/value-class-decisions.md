# Value class decisions

Each freedriver value class that cannot become a record as it stands gets one decision: a sealed interface with record implementations, or left as it is, with the reason. Follow-up cards below carry out the changes. This document files none of them. The Project Manager files them from this list.

Types were read from `kazetsukaimiko/freedriver` `origin/master` `6fb11d409945a82468b1c56fa089928848a5914f`. Autonomy importers were read from `kazetsukaimiko/autonomy` `origin/master` `270c6530a1bfe2520ced881a2dd33fac69683e36`. A record cannot extend a class and cannot be abstract. The shape already used for value types is an immutable record with Lombok `@Builder(toBuilder = true)`. The in-repo pattern for a sealed interface with record implementations is `DevicePathSupplier` (`jsonlink` `config/DevicePathSupplier.java`), which permits `DeviceByPath` and `DeviceByVendorAndDeviceId` and binds with `@JsonTypeInfo(use = Id.DEDUCTION)` plus `@JsonSubTypes`.

`v3 Mappings` is already that kind of record. It does not extend `ConfigFile`, so it is not in the decision table. Condition, Hint, and Hint's subclasses are decided in `docs/condition-and-hint-decisions.md` (kazetsukaimiko/freedriver#49). `FirmwareVersion` is already a record; `FirmwareVersionConverter` stays as it is.

## Release order

Tag `1.0.0` is published (pull request #56, which closed kazetsukaimiko/freedriver#48). `.github/release/publish_jsonlink.sh` deploys one allowlist: the parent POM, the three BOMs (`bom`, `jakarta-bom`, `3p-bom`), and the jars `jsonlink`, `math-jpa`, `victron`, `victron-jpa`, `inotify-cdi`, and `metrics`. `-am` also deploys `serial-impl`, `serial-api`, `base`, `math`, and `inotify`. Autonomy pins `1.0.0` (kazetsukaimiko/autonomy#104).

The root `pom.xml` version is still `1.0.0-SNAPSHOT` (line 7). `.github/workflows/publish-jsonlink.yml` runs `.github/release/version_bump.py` `check_tag` before a tag publishes. The tag equals the root version with `-SNAPSHOT` removed, and it equals the conventional bump from the previous tag. From `1.0.0`, a breaking change is `2.0.0`, a feat is `1.1.0`, and a fix is `1.0.1`. Refactor and docs leave the tag unchanged. While the committed version stays `1.0.0-SNAPSHOT`, the Maven release version is `1.0.0`, so `check_tag` refuses every later tag. A later tag is accepted only after a commit sets the root version to the conventional bump from `1.0.0`: `2.0.0-SNAPSHOT` for a breaking change, `1.1.0-SNAPSHOT` for a feat, `1.0.1-SNAPSHOT` for a fix. `versions:set` in the deploy job is uncommitted, so the gate still reads the committed pom. `distributionManagement` has one repository (`maven.pkg.github.com/kazetsukaimiko/freedriver`) and no `snapshotRepository`, so the tag fills a release version and a `1.0.0-SNAPSHOT` pin stays unresolved. This card changes neither `.github/release/publish_jsonlink.sh` nor `bom/pom.xml`.

A follow-up that changes a public signature of `Measurement` or v2 `Mappings` ships on the next tag, after a commit sets the root version to that bump. Autonomy moves from the `1.0.0` pin to that tag. The type decisions in this document stay as written. `ScaledNumber` stays a class, so it has no signature-changing follow-up.

A decision that changes a public signature names the autonomy source below. A decision that keeps the public signature says that no autonomy release is required. `daly` is absent from autonomy's classpath. Types autonomy does not import (`daly`, `Pin`, `ControlMapping`, `v3 Descriptor`, `JoystickButtonEvent`, `v1 Mappings`) merge with no autonomy release. Each follow-up card branches from `master`, merges on its own, and leaves the reactor green under `mvn verify`.

## daly `Request`

**Decision:** sealed interface with record implementations.

`Signal` becomes a sealed interface permitting `Request`, `ReadRequest`, and `Response`. `Request` becomes a record. `ReadRequest` is a sibling record, not a subclass: a record is final, so `ReadRequest` cannot keep extending `Request`.

Today `ReadRequest` extends `Request` and its constructors call `super(address, queryId, (byte) 0, DalyCommand.READ, new byte[8])` (the one-argument form uses `Address.UPPER`). `Request` also has `Request(DalyCommand, byte[])`, which fills null address and query id, and a builder constructor. Both records keep `@Builder(toBuilder = true)`. `ReadRequest` exposes the same two factories and always stores `DalyCommand.READ` and an 8-byte payload. `Request(DalyCommand, byte[])` stays a factory on the `Request` record.

**Equality after the split.** `Signal.equals` is the same runtime class, the same address, and the same query id. Payload and checksum stay out of equality, which is today's `getClass()` plus address plus query id. `hashCode` is `Objects.hash(address, queryId)`, matching that equality. `Request` and `ReadRequest` with the same address and query id stay unequal because the runtime class differs.

**Who owns the Daly payload bytes.** Each record owns the bytes it stores. `Request` owns the caller-supplied `data` array; its compact constructor derives `dataLength` from `data.length` (0 when `data` is null) and masks `checksum` with `0xFF`, as the class constructor does today. `ReadRequest` owns only the fixed 8-byte READ payload its factories pass; it has no second payload parameter. `Response` owns the parsed `data`. One default method on `Signal` builds the frame bytes (start flag, address, command or query id, length, payload, checksum, trailing newline) from those components. `ReadRequest` does not keep a second copy of the frame rules. Today's `Signal.validates()` calls an instance `toBytes()` that `Signal` does not declare; the follow-up defines that frame method once and points `validates()` at it.

**Release.** No autonomy source imports `daly`. No autonomy release is required.

**Saved format.** Daly frames. The implementation card's fixtures keep the frame bytes for a `ReadRequest` (8-byte READ payload, address, query id) and the address-and-queryId equality (same class, same address, same query id, different payload, still equal; `Request` and `ReadRequest` unequal).

## daly `Response`

**Decision:** sealed interface with record implementations, under the same `Signal` as `Request`.

`Response` is already an immutable class with `@Builder(toBuilder = true)` and final fields, and it extends `Signal`. It becomes a record permitted by `Signal`, with the same components (`address`, `queryId`, `checksum`, `dataLength`, `data`) and the same checksum mask. Equality and frame ownership are the `Signal` contract above.

**Release.** No autonomy release is required.

## jsonlink `ConfigFile`, config `Mappings` (v1), config.v2 `Mappings`

**Decision:** sealed interface with record implementations. One follow-up card owns the `ConfigFile` change and both `Mappings` records.

`ConfigFile` is one abstract method, `getFileName()`. It becomes a sealed interface with that method, permitting `io.freedriver.jsonlink.config.Mappings` and `io.freedriver.jsonlink.config.v2.Mappings`.

v1 `Mappings` becomes a record implementing `ConfigFile` and `Migration` to v2. The component stays `Set<Mapping> mappings`. `getFileName()` stays `"mappings.json"`. `migrate()` and `migrateMapping()` stay: each v1 `Mapping` becomes a v2 `Mapping` whose appliances are the pin names. The record keeps a `getMappings()` bean method and `@Builder(toBuilder = true)` so existing builders and getters keep compiling.

v2 `Mappings` becomes a record implementing `ConfigFile`. It is `@Deprecated`, and `getFileName()` stays `"mappings_v2.json"`. Components stay `eventTTL`, `eventTTLUnit`, `mappings`, and `startupActions`, including today's defaults (TTL 7 days). The record keeps `getEventTTL()`, `getEventTTLUnit()`, `getMappings()`, `getStartupActions()`, and the builder.

**Startup actions.** The `startupActions` component keeps `@JsonDeserialize(using = StartupActionsDeserializer.class)`. The record's canonical constructor copies the map; it is not the binder. `StartupActionsDeserializer` keeps resolving type ids only through `MappingsAction.registeredTypes()`, checks every type in a board's list before constructing any action, and refuses a class name. `MappingsActionTypeIdResolver` (class declaration at `MappingsActionTypeIdResolver.java` line 16; the comment above it starts at line 13) and `PhoneNumberTypeIdResolver` are the pattern: `typeFromId` looks up a logical name in a map of classes already registered in code. A raw `Map` binding in the canonical constructor is not the replacement for that deserializer.

**Release.** Autonomy at `270c653` constructs v2 `Mappings` and calls the bean getters in:

- `quarkus/.../config/MappingsDirectoryLoader.java` (`getMappings`, `getStartupActions`, `getEventTTL`, `getEventTTLUnit`)
- `quarkus/.../service/startup/StartupMappingsLoader.java`
- `quarkus/.../mqtt/CacheApplianceStateSource.java` (`getMappings`)
- `quarkus/.../service/SimpleAliasService.java` (`getMappings`)
- `quarkus/.../service/crud/TTLEnforcementService.java` (`getEventTTL`, `getEventTTLUnit`)

plus the v2 types those files already import: `Mapping`, `Appliance`, `AnalogAlert`, `AnalogSensor`, and `MappingsAction`. v1 `Mappings` has no autonomy importer. Saved `mappings.json` still loads through v1.

Keeping those getters and the builder means those files keep compiling. The type still changes from class to record, so this is a public signature change of v2 `Mappings`. It ships on the next tag, after a commit sets the root version to that bump, and autonomy moves from the `1.0.0` pin to that tag. Dropping `getMappings` or the TTL getters is what would stop the files above compiling. No autonomy source names `ConfigFile`.

**Saved format.** `mappings.json` and `mappings_v2.json`, and the v1-to-v2 `migrate()` path, stay loadable. The implementation card's fixtures cover a v2 file whose `startupActions` use logical names, and a file whose `type` is a class name, which the deserializer refuses before any action is constructed.

## jsonlink v3 `Descriptor`

**Decision:** sealed interface with record implementations.

`Descriptor` becomes a sealed interface permitting `ApplianceDescriptor`, `EventDescriptor`, and `ToggleAction`. Each is a record with one `String` component, `@JsonValue` on the string, and `@JsonCreator` on the canonical constructor, so saved JSON stays a JSON string.

`ToggleAction` is a sibling record. It does not extend `ApplianceDescriptor`, because a record is final. Today `ToggleAction extends ApplianceDescriptor`, and `ApplianceDescriptor` and `EventDescriptor` add only a `@JsonCreator` constructor over `Descriptor`. `v3 Mappings` stores them in different positions: `Map<ApplianceDescriptor, Appliance>`, `Map<EventDescriptor, ControlEvent>`, and `Map<EventDescriptor, List<ToggleAction>>`. No in-repo caller assigns a `ToggleAction` to an `ApplianceDescriptor` variable. `ControlMapping`'s value type stays `List<ApplianceDescriptor>` and is unaffected.

**Equality after the split.** Lombok `@EqualsAndHashCode` on `Descriptor` uses the runtime class and the string. `ToggleAction` inherits that `equals`, so a `ToggleAction` and an `ApplianceDescriptor` with the same string are not equal. The records keep that contract: equality is the runtime class plus the string, and `hashCode` hashes that same pair. `v3 Mappings` uses the descriptors as map keys, so a `ToggleAction` remains a different key from an `ApplianceDescriptor` with the same string.

**Release.** Autonomy does not import these types. No autonomy release is required.

**Saved format.** v3 descriptor keys are the file name (`Mappings.descriptor` uses `Path.getFileName()`), and toggle values are JSON strings. The implementation card's fixtures keep an `ApplianceDescriptor` and a `ToggleAction` with the same string unequal, and keep them usable as distinct map keys.

## jsonlink `JoystickButtonEvent`

`ControlEvent`'s wire contract, including `JoystickButtonEvent`, is kazetsukaimiko/freedriver#57.

## jsonlink `ControlMapping`

**Decision:** left as it is.

`ControlMapping` extends `LinkedHashMap<EventDescriptor, List<ApplianceDescriptor>>` and adds no fields or methods. A record cannot extend `LinkedHashMap`, and a record that wrapped the map would stop being a `Map`. The class has no other reference on `origin/master`. It is a map, not a value. Leaving it as a class keeps the `Map` type for any saved code that still constructs it.

**Release.** No public signature change. No autonomy release is required. No follow-up card.

## jsonlink `Pin`

**Decision:** left as it is.

`pin.Pin` is abstract, has no subclass, and its constructor calls `connector.send(...)`. It is an I/O object: it holds a `Connector`, an `Identifier`, and a `Mode`, and it performs a pin operation. A sealed interface or a record is not an outcome for `Pin`. `Pin` stays off every `ObjectMapper` path, `ConfigMapper` included. `ConfigMapper.MAPPER` is a plain `ObjectMapper` with `JsonLinkModule` and indent enabled; `Pin` is not registered there and stays unregistered.

**Release.** No public signature change. No autonomy release is required. No follow-up card.

## math `Measurement` and `TemporalMeasurement`

**Decision:** sealed interface with record implementations. This drops `extends Number`.

Today `Measurement<M>` is an abstract class extending `NumberDelegate` (which extends `Number`) and implementing `Scaleable<M>` and `NumberOperations<M>`. `value` and `unit` are not final. A public no-arg constructor exists on `Measurement`, on `TemporalMeasurement`, on `FixedMeasurement`, on `InstantaneousMeasurement`, and on every leaf. In-repo code has no `new Current()` (or other leaf) call of that no-arg constructor. `math-jpa` converters call the `ScaledNumber` constructors. Autonomy's `MeasurementDeserializer` calls those same constructors through a `Function<ScaledNumber, M>`.

The hierarchy on `origin/master`:

| Type | Kind | Leaf or parent | Unit |
| --- | --- | --- | --- |
| `FixedMeasurement` | abstract | parent of the fixed leaves | taken from the leaf |
| `InstantaneousMeasurement` | abstract | parent of the instantaneous leaves | taken from the leaf |
| `TemporalMeasurement` | abstract | parent of `Energy`; adds `temporalUnit` | `SIElectricalUnit` plus `TemporalUnit` |
| `Current` | class | instantaneous | `AMPS`, fixed in the constructor |
| `Power` | class | instantaneous, also `ToTemporal<Energy>` | `WATTS`, fixed in the constructor |
| `Potential` | class | fixed | `VOLTS`, fixed in the constructor |
| `Resistance` | class | fixed | `OHMS`, fixed in the constructor |
| `Charge` | class | fixed | `COULOMBS`, fixed in the constructor |
| `Temperature` | class | fixed | `(ScaledNumber, TemperatureScale)` |
| `Energy` | class | extends `TemporalMeasurement` | `WATTS` and `TemporalUnit.HOURS`, fixed in the constructor |

`Power.hours(Number)` returns `new Energy(getValue().multiply(ScaledNumber.of(multiplier)))`.

The follow-up shape:

- `Measurement<M extends Measurement<M>>` becomes a sealed interface extending `Scaleable<M>`, permitting `FixedMeasurement`, `InstantaneousMeasurement`, and `TemporalMeasurement`.
- Those three become sealed interfaces. `FixedMeasurement` permits `Potential`, `Resistance`, `Charge`, and `Temperature`. `InstantaneousMeasurement` permits `Current` and `Power`. `TemporalMeasurement` permits `Energy`.
- Each leaf becomes a record. Electrical leaves take `ScaledNumber` and fix the unit in the constructor, as they do today. `Temperature` takes `(ScaledNumber, TemperatureScale)`. `Energy` takes `ScaledNumber` and fixes `WATTS` and `HOURS`. `Power` also implements `ToTemporal<Energy>`.
- `getFullUnit()` stays on the family that owns it today. Fixed and instantaneous leaves return `value.toPrefixString() + unit.getSymbol()`. `Energy` returns `value.toPrefixString() + unit.getSymbol() + temporalUnit.getSuffix()`. `toString()` keeps returning `getFullUnit()`, because `StringMeasurementConverter.convertToDatabaseColumn` and `StringTemporalMeasurementConverter` store `m.toString()` and parse that text back.
- `getValue()` stays as a method returning the `ScaledNumber`, so autonomy `MeasurementSerializer` (`m.getValue().descale()`) and `SocRange` (`subtract` / `getValue`) keep compiling.
- The public no-arg constructors go away with the abstract classes. Converters, Jackson in autonomy, and JPA go through the `ScaledNumber` constructors and the `AttributeConverter`s, which is the path this design keeps.
- `NumberDelegate` is removed from this hierarchy. `intValue`, `longValue`, `floatValue`, and `doubleValue` move onto `Measurement` and delegate to the `ScaledNumber`, which is what `NumberDelegate` does through `getDelegateNumber()`. The type no longer extends `Number`. `NumberOperations` is bounded by `T extends Number`, so the follow-up widens that bound and `Measurement` keeps `add`, `subtract`, `multiply`, `divide`, and `abs`. A record cannot extend `Number`, so this sealed-record decision is a public signature change under the release order above.

**Equality.** Today `Measurement.equals` is the same runtime class and `value.compareTo(that.value) == 0` (the unit is ignored). `hashCode` is `Objects.hash(value, unit)`, which does not match `equals`. `TemporalMeasurement.equals` adds `temporalUnit`, and its `hashCode` mixes `super.hashCode()` with `temporalUnit`. The record version keeps equality by value through `compareTo`, with the same runtime class. `hashCode` changes to the same key `equals` uses (the rounded compare-to value, plus `temporalUnit` for `Energy`), so the two agree. Canonical record equality, which would include every component and the raw `ScaledNumber` fields, is not the contract.

**Temperature scale.** `Temperature.construct` rebuilds with `TemperatureScale.CELSUIS`. `TemperatureConverter.convertToEntityAttribute` builds `new Temperature(ScaledNumber.of(value, unitPrefix), TemperatureScale.CELSUIS)`. The column stores only `scaleTo(ONE).getValue().getValue()` as a `BigDecimal`, so the scale is not in the column. The record keeps that: arithmetic and `scaleTo` produce a `Temperature` on `CELSUIS`, and the converter keeps supplying `CELSUIS` when it reads the `BigDecimal`. Two temperatures that `compareTo` treats as the same number stay equal, including when the scale instance differs, because today's `equals` ignores `unit`.

**`AttributeConverter`s the math card updates in the same pull request.** `MeasurementConverter` and `TemporalMeasurementConverter` store `scaleTo(ONE).getValue().getValue()` as a `BigDecimal`. A `TemporalMeasurement`'s unit is not stored; `EnergyConverter` rebuilds `HOURS` by calling `new Energy(ScaledNumber.of(value, unitPrefix))`. `StringMeasurementConverter` and `StringTemporalMeasurementConverter` store `m.toString()`. Concrete converters in that pull request: `CurrentConverter`, `EnergyConverter`, `PotentialConverter`, `PowerConverter`, `TemperatureConverter`. `victron` `Column.normalized` is bounded by `M extends Measurement<M>` and moves with this card, as do its callers (`ColumnGenerator` included). `Charge` and `Resistance` have no converter class; the card still converts the types and `mvn verify` covers them. `FirmwareVersionConverter` stays as it is.

**Release.** This is a public signature change of `math`. It ships on the next tag, after a commit sets the root version to that bump, and autonomy moves from the `1.0.0` pin to that tag. Autonomy source that imports the types, and that stops compiling if `getValue()`, the `ScaledNumber` constructors, or the arithmetic methods are dropped:

- `jpa/.../entity/VEDirectMessage.java` (`Current`, `Energy`, `Potential`, `Power`, and `FirmwareVersion`)
- `jpa/.../entity/event/sensor/TemperatureEvent.java`
- `jpa/.../entity/math/Chemistries.java`, `SocRange.java`, `StateOfChargeConfig.java`, `VoltageSoc.java`
- `api/src/test/.../entity/math/SocRangeTest.java`
- `quarkus/.../vedirect/VEDirectMessageChange.java`
- `quarkus/.../vedirect/jackson/MeasurementSerializer.java`, `MeasurementDeserializer.java`, `VEDirectModule.java`

At `270c653` those files use the constructors, `getValue()`, and `subtract`. They do not mention `NumberDelegate`. The follow-up keeps the constructors and `getValue()`, so the compile break to avoid is a missing `getValue` or a missing `new Potential(ScaledNumber)` (and the same for `Current`, `Power`, `Energy`, and `Temperature`). `math-jpa` is on the `1.0.0` allowlist. The follow-up's tag publishes `math-jpa` from the same commit as the new `math`, and autonomy moves from the `1.0.0` pin to that tag.

## math `ScaledNumber`

**Decision:** left as it is.

`ScaledNumber` extends `Number` and implements `NumberOperations<ScaledNumber>` and `Scaleable<ScaledNumber>`. A record cannot extend `Number`, and `NumberOperations` requires `T extends Number`. The class also carries the constants `ZERO`, `ONE`, and `TEN` and the `of(...)` factories autonomy already calls (`Chemistries`, `StateOfChargeConfig`, `SocRangeTest`).

**Equality a record version would have to keep, which is why this decision does not take one.** `equals` is the same class and `round().compareTo(that.round()) == 0`, with rounding through `scaleTo(ONE)`. `hashCode` today is `Objects.hash(value, unitPrefix)`, which is not that key. A canonical record `equals` would compare `value`, `unitPrefix`, and `scale` as raw components and would change the contract `compareTo` and the converters rely on. Leaving the class in place keeps the `Number` signature and that `compareTo` equality. No follow-up card converts `ScaledNumber`.

**Release.** No public signature change. No autonomy release is required. The `Measurement` card uses `ScaledNumber` as a component and does not modify this class.

## v3 layout the migrations keep

`v3 Mappings.load` reads three directories under the config provider, each filtered to `*.json`:

- `jsonlink/mappings/*.json` loads as `List<ToggleAction>`. The file name becomes the `EventDescriptor` key.
- `jsonlink/mappings/appliances/*.json` loads as `Appliance`. The file name becomes the `ApplianceDescriptor` key.
- `jsonlink/mappings/controls/*.json` loads as `ControlEvent`. The file name becomes the `EventDescriptor` key.

`v2 Mapping.migrate()` builds that object graph in memory from `controlMap`, a `Map<String, List<String>>`. Each key is split on `:`. Index 0 is the button number. Index 1 equal to `"0"` is `ButtonState.RELEASE`; any other index-1 text is `PRESS`. The `EventDescriptor` is the appliance names joined with `_AND_`. Each appliance name becomes a `ToggleAction`. The value list is the toggle list for that descriptor. This path constructs `JoystickButtonEvent` with the builder. Saved control files are kazetsukaimiko/freedriver#57.

## Follow-up cards

The Project Manager files these. Each branches from `master` and merges on its own. Each leaves `mvn verify` green for the whole reactor. Stacking one on an unmerged pull request is not part of this order.

1. **daly: `Signal` sealed, `Request`, `ReadRequest`, and `Response` as records.** Sibling `ReadRequest`, equality, and frame ownership as decided above. Fixtures: frame bytes and address-and-queryId equality. No autonomy release. `daly` is outside the publish allowlist.
2. **jsonlink `ControlEvent` and `JoystickButtonEvent`.** The wire contract is kazetsukaimiko/freedriver#57.
3. **jsonlink: `ConfigFile` as a sealed interface, v1 and v2 `Mappings` as records.** This is the one card that edits `ConfigFile`. Keeps `StartupActionsDeserializer`. Ships on the next tag, after a commit sets the root version to that bump. Autonomy moves from the `1.0.0` pin to that tag. Autonomy files listed under v2 `Mappings` keep compiling while the bean getters stay. Fixtures: `mappings.json`, `mappings_v2.json`, v1-to-v2 migration, logical `startupActions` types, and a refused class name.
4. **math: `Measurement` sealed records, including the leaves and `TemporalMeasurement`.** Same pull request updates `math-jpa` converters (`MeasurementConverter`, `TemporalMeasurementConverter`, `StringMeasurementConverter`, `StringTemporalMeasurementConverter`, `CurrentConverter`, `EnergyConverter`, `PotentialConverter`, `PowerConverter`, `TemperatureConverter`) and `victron` `Column`. Ships on the next tag, after a commit sets the root version to that bump. Autonomy moves from the `1.0.0` pin to that tag. Autonomy files listed under `Measurement` are the compile set. `ScaledNumber` stays a class. `FirmwareVersionConverter` stays as it is.

`Pin` and `ControlMapping` have no card. #48 is closed: tag `1.0.0` published the parent, the three BOMs, and the allowlist jars. #49 is closed: its decision is `docs/condition-and-hint-decisions.md`. This document adds no scope to either.
