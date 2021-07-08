package com.dpaula.clientesapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @author Fernando de Lima
 */
public class JsonUtilsTest {

    /**
     * Converte objeto em uma string no formato json
     *
     * @param obj de entrada para conversão
     * @return string json
     */
    public static String toJson(final Object obj) throws JsonProcessingException {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .writer()
            .withDefaultPrettyPrinter()
            .writeValueAsString(obj);
    }
}
