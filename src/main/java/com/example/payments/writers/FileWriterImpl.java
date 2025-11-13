package com.example.payments.writers;

import com.example.payments.engine.interfaces.OutputWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

/**
 * Appends JSON payloads to a file. Parameter: path
 */
public class FileWriterImpl implements OutputWriter {
    private ObjectMapper mapper;
    private File file;

    @Override
    public void init(Map<String, Object> parameters) throws Exception {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String path = parameters != null && parameters.containsKey("path") ? parameters.get("path").toString() : "results.json";
        file = new File(path);
        if (!file.exists()) Files.createDirectories(file.getParentFile().toPath());
    }

    @Override
    public synchronized void write(Map<String, Object> payload) throws Exception {
        // Append as newline-delimited JSON for simplicity
        String json = mapper.writeValueAsString(payload);
        Files.writeString(file.toPath(), json + System.lineSeparator(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
    }

    @Override
    public String name() { return "FileWriter"; }
}