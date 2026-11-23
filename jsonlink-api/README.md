# jsonlink-api

The jsonlink board schema as a contract: the records and enums a board request and response are
made of, in package `io.freedriver.jsonlink.api.v1`. The types describe their own JSON with
Jackson annotations, so any `ObjectMapper` reads and writes them with nothing registered. The
module's only dependency is `jackson-annotations`.

| JSON | Type | Wire form |
| --- | --- | --- |
| request | `BoardRequest` | object; property names are the component names (`turn_off`, `turn_on` included) |
| response | `BoardResponse` | object; property names are the component names |
| pin, as a value | `Identifier` | number, `13` |
| pin, as a map key | `Identifier` | the pin's decimal digits, `"13"` |
| analog pin | `AnalogIdentifier` | text, `"A3"` |
| mode | `Mode` | boolean: `OUTPUT` is `true`, `INPUT` is `false` |
| digital state | `DigitalState` | boolean: `LOW` is `true`, `HIGH` is `false` |
| version | `Version` | array of three numbers, `[1, 2, 3]` |

```java
BoardRequest request = BoardRequest.empty()
        .modeSet(Identifier.of(13).setMode(Mode.OUTPUT))
        .digitalWrite(Identifier.of(13).setDigital(DigitalState.LOW));
String json = new ObjectMapper().writeValueAsString(request);
BoardResponse response = new ObjectMapper().readValue(reply, BoardResponse.class);
```
