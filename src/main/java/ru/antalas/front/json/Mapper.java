package ru.antalas.front.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Mapper {
    private final ObjectMapper mapper = new ObjectMapper();

    public String json(Object in) throws JsonProcessingException {
        return mapper.writeValueAsString(in);
    }
}
