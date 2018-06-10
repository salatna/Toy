package ru.antalas.front.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class Mapper {
    private final ObjectMapper mapper = new ObjectMapper();

    public String json(Object in) {
        try {
            return mapper.writeValueAsString(in);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T fromInputStream(InputStream is, TypeReference<T> typeRef) {
        try {
            return mapper.readValue(is, typeRef);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public static class JsonException extends RuntimeException {
        private JsonException(Exception ex) {
            super(ex);
        }
    }
}
