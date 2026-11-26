package io.freedriver.jsonlink.config;

import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public interface ConfigMapper {
    ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    Logger LOGGER = Logger.getLogger(ConfigMapper.class.getName());
}
