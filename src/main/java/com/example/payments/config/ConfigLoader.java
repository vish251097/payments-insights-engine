package com.example.payments.config;

import com.example.payments.utils.ErrorHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Loads YAML configuration into RunConfig.
 */
public class ConfigLoader {
    public static RunConfig load(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            return mapper.readValue(new File(path), RunConfig.class);
        } catch (FileNotFoundException fnf) {
            ErrorHandler.log("ConfigLoader", "Configuration file not found: " + path);
        } catch (Exception e) {
            ErrorHandler.log("ConfigLoader", e);
        }
        return null; // fail-safe

}
}