# Changelog

## [2.0.0](https://github.com/kazetsukaimiko/freedriver/compare/1.0.0...2.0.0) (2026-11-27)


### ⚠ BREAKING CHANGES

* **jsonlink:** The schema package io.freedriver.jsonlink.jackson.schema.v1 moves to io.freedriver.jsonlink.api.v1. Request and Response become BoardRequest and BoardResponse. Removed io.freedriver.jsonlink.jackson.schema.base.Version, io.freedriver.jsonlink.jackson.schema.base.BaseResponse, JsonLinkModule, AnalogPinNumberKeyDeserializer, AnalogPinNumberSerializer, DigitalStateSerializer, ModeSerializer, PinNumberDeserializer, PinNumberKeyDeserializer, and PinNumberSerializer. Removed Request.invoke, Response.version, Response.created, Response.createdAt, Mode.fromBoolean, and builder() and toBuilder() on Request, Response, and Identifier. Connector.send, Connector.makeRequest, Connector.getResponse, and AnalogSensor.applyToRequest take and return the api.v1 types.

### Features

* **jsonlink-api:** add the jsonlink schema contract module ([#80](https://github.com/kazetsukaimiko/freedriver/issues/80)) ([27751c6](https://github.com/kazetsukaimiko/freedriver/commit/27751c6aaedde97c9aae0c08c5afe6c71199540d))
* **jsonlink:** move jsonlink onto the jsonlink-api schema ([#90](https://github.com/kazetsukaimiko/freedriver/issues/90)) ([348ce07](https://github.com/kazetsukaimiko/freedriver/commit/348ce07552947cfbe7f543da0f16d8fa23bee91d)), closes [#74](https://github.com/kazetsukaimiko/freedriver/issues/74)


### Bug Fixes

* **bom:** manage only the published allowlist jars ([#71](https://github.com/kazetsukaimiko/freedriver/issues/71)) ([37fa67a](https://github.com/kazetsukaimiko/freedriver/commit/37fa67a4ce5fa37aa2b764561f39bd2a61e49d83))
* **jsonlink:** read every toggle and appliance json file ([#68](https://github.com/kazetsukaimiko/freedriver/issues/68)) ([f0ac619](https://github.com/kazetsukaimiko/freedriver/commit/f0ac619dcdf861b7fdad24a3352a6c4fe5bb4a10)), closes [#64](https://github.com/kazetsukaimiko/freedriver/issues/64)
