package io.freedriver.jsonlink.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.freedriver.jsonlink.jackson.JsonLinkModule;

import java.util.logging.Logger;

public interface ConfigMapper {
    ObjectMapper MAPPER = new ObjectMapper().registerModule(new JsonLinkModule()).enable(SerializationFeature.INDENT_OUTPUT);
    Logger LOGGER = Logger.getLogger(ConfigMapper.class.getName());
}
