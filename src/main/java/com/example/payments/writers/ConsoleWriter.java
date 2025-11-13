package com.example.payments.writers;

import com.example.payments.engine.interfaces.OutputWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Map;

/**
 * Pretty prints analytics payload to console (System.out).
 */
public class ConsoleWriter implements OutputWriter {
    private ObjectMapper mapper;

    @Override
    public void init(Map<String, Object> parameters) throws Exception {
        mapper = new ObjectMapper();
        // Register Java 8 date/time module so OffsetDateTime is supported
        mapper.registerModule(new JavaTimeModule());
        // Emit ISO-8601 strings instead of timestamps (numbers)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // If you want automatic module discovery instead, you can use:
        // mapper.findAndRegisterModules();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void write(Map<String, Object> payload) throws Exception {
        System.out.println(mapper.writeValueAsString(payload));
    }

    @Override
    public String name() { return "ConsoleWriter"; }
}