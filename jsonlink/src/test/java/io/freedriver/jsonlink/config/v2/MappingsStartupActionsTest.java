package io.freedriver.jsonlink.config.v2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.freedriver.jsonlink.config.ConfigMapper;
import io.freedriver.jsonlink.jackson.JsonLinkModule;
import io.freedriver.jsonlink.jackson.schema.v1.Identifier;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MappingsStartupActionsTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void resetConstructions() {
        NoteMappingsAction.constructed.set(0);
        FlagMappingsAction.constructed.set(0);
    }

    @Test
    void registryListsLogicalNamesWithoutConstructingActions() {
        assertEquals(0, NoteMappingsAction.constructed.get());
        assertEquals(0, FlagMappingsAction.constructed.get());

        Map<String, Class<? extends MappingsAction>> types = MappingsAction.registeredTypes();

        assertEquals(Set.of("note", "flag"), types.keySet());
        assertEquals(NoteMappingsAction.class, types.get("note"));
        assertEquals(FlagMappingsAction.class, types.get("flag"));
        assertEquals(0, NoteMappingsAction.constructed.get());
        assertEquals(0, FlagMappingsAction.constructed.get());
    }

    @Test
    void roundTripsEachRegisteredType() throws Exception {
        ObjectMapper configMapper = ConfigMapper.MAPPER;
        for (Class<? extends MappingsAction> type : MappingsAction.registeredTypes().values()) {
            MappingsAction sample = sample(type);
            for (ObjectMapper reader : List.of(mapper, configMapper)) {
                String json = reader.writeValueAsString(sample);
                assertFalse(json.contains(type.getName()), json);
                MappingsAction back = reader.readValue(json, MappingsAction.class);
                assertEquals(sample, back);
                assertEquals(sample.type(), back.type());
            }
        }
    }

    @Test
    void fileWithoutStartupActionsLoadsAsBefore() throws JsonProcessingException {
        String json = """
                {
                  "eventTTL": 7,
                  "eventTTLUnit": "DAYS",
                  "mappings": [{
                    "connectorId": "0f2829e1-1804-4993-ae33-c5dd21840646",
                    "appliances": [{"identifier": 26, "name": "water_inlet"}]
                  }]
                }
                """;
        Mappings mappings = JsonLinkModule.getMapper().readValue(json, Mappings.class);
        assertEquals(7, mappings.getEventTTL());
        assertEquals(ChronoUnit.DAYS, mappings.getEventTTLUnit());
        assertEquals(Map.of(), mappings.getStartupActions());
        assertEquals(1, mappings.getMappings().size());
        Mapping mapping = mappings.getMappings().iterator().next();
        assertEquals(Identifier.of(26), mapping.appliances().get(0).identifier());
        assertEquals(0, NoteMappingsAction.constructed.get());
    }

    @Test
    void nullStartupActionsLoadsAsEmpty() throws JsonProcessingException {
        Mappings mappings = mapper.readValue("{\"startupActions\": null}", Mappings.class);
        assertEquals(Map.of(), mappings.getStartupActions());
    }

    @Test
    void roundTripsStartupActionsInOrder() throws JsonProcessingException {
        String json = """
                {
                  "startupActions": {
                    "board-b": [
                      {"type": "flag", "on": true},
                      {"type": "note", "note": "second"}
                    ],
                    "board-a": [
                      {"type": "note", "note": "first"}
                    ]
                  }
                }
                """;
        Mappings mappings = mapper.readValue(json, Mappings.class);
        assertEquals(List.of("board-b", "board-a"), List.copyOf(mappings.getStartupActions().keySet()));
        assertEquals(
                List.of(new FlagMappingsAction(true), new NoteMappingsAction("second")),
                mappings.getStartupActions().get("board-b"));
        assertEquals(List.of(new NoteMappingsAction("first")), mappings.getStartupActions().get("board-a"));
        assertEquals(mappings, mapper.readValue(json, Mappings.class));

        JsonNode flag = mapper.readTree(mapper.writeValueAsString(new FlagMappingsAction(false)));
        assertEquals("flag", flag.get("type").asText());
        assertEquals(false, flag.get("on").asBoolean());
        assertEquals(2, flag.size());
    }

    @Test
    void unknownTypeRefusesTheWholeListBeforeAnyActionIsConstructed() {
        String json = """
                {
                  "startupActions": {
                    "board-ok": [{"type": "note", "note": "kept"}],
                    "board-bad": [
                      {"type": "note", "note": "before"},
                      {"type": "not-registered", "note": "nope"}
                    ]
                  }
                }
                """;
        JsonProcessingException error = assertThrows(
                JsonProcessingException.class, () -> mapper.readValue(json, Mappings.class));
        assertTrue(error.getMessage().contains("not-registered"), error.getMessage());
        assertTrue(error.getMessage().contains("board-bad"), error.getMessage());
        assertTrue(error.getMessage().contains("refusing the board's startupActions"), error.getMessage());
        assertEquals(0, NoteMappingsAction.constructed.get());
        assertEquals(0, FlagMappingsAction.constructed.get());
    }

    @Test
    void classNameInTypeIsRefused() {
        String className = NoteMappingsAction.class.getName();
        String json = """
                {
                  "startupActions": {
                    "board-1": [{"type": "%s", "note": "nope"}]
                  }
                }
                """.formatted(className);
        JsonProcessingException error = assertThrows(
                JsonProcessingException.class, () -> mapper.readValue(json, Mappings.class));
        assertTrue(error.getMessage().contains(className), error.getMessage());
        assertEquals(0, NoteMappingsAction.constructed.get());

        JsonProcessingException direct = assertThrows(
                JsonProcessingException.class,
                () -> mapper.readValue("{\"type\":\"" + className + "\",\"note\":\"nope\"}", MappingsAction.class));
        assertTrue(direct.getMessage().contains(className), direct.getMessage());
        assertEquals(0, NoteMappingsAction.constructed.get());
        assertNull(mapper.getDeserializationConfig().getDefaultTyper(mapper.constructType(MappingsAction.class)));
        assertNull(ConfigMapper.MAPPER.getDeserializationConfig()
                .getDefaultTyper(ConfigMapper.MAPPER.constructType(MappingsAction.class)));
        ObjectMapper moduleMapper = JsonLinkModule.getMapper();
        assertNull(moduleMapper.getDeserializationConfig()
                .getDefaultTyper(moduleMapper.constructType(MappingsAction.class)));
    }

    @Test
    void startupActionsTypeCascadesValidationToEachAction() throws Exception {
        Field field = Mappings.class.getDeclaredField("startupActions");
        AnnotatedParameterizedType map = (AnnotatedParameterizedType) field.getAnnotatedType();
        AnnotatedParameterizedType list =
                (AnnotatedParameterizedType) map.getAnnotatedActualTypeArguments()[1];
        AnnotatedType action = list.getAnnotatedActualTypeArguments()[0];
        boolean cascaded = false;
        for (Annotation annotation : action.getDeclaredAnnotations()) {
            if (annotation instanceof Valid) {
                cascaded = true;
            }
        }
        assertTrue(cascaded);

        String json = """
                {"startupActions":{"board-1":[{"type":"note","note":null}]}}
                """;
        Mappings mappings = mapper.readValue(json, Mappings.class);
        try (ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<Mappings>> violations = validator.validate(mappings);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(violation -> {
                String path = violation.getPropertyPath().toString();
                return path.contains("startupActions") && path.contains("note");
            }), violations.toString());

            Mappings valid = mapper.readValue(
                    "{\"startupActions\":{\"board-1\":[{\"type\":\"note\",\"note\":\"ok\"}]}}", Mappings.class);
            assertEquals(Set.of(), validator.validate(valid));
        }
    }

    private static MappingsAction sample(Class<? extends MappingsAction> type) {
        if (type == NoteMappingsAction.class) {
            return new NoteMappingsAction("round-trip");
        }
        if (type == FlagMappingsAction.class) {
            return new FlagMappingsAction(true);
        }
        throw new IllegalStateException("no sample for registered type " + type.getName());
    }
}
