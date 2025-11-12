package com.example.payments.writers;

import com.example.payments.engine.interfaces.OutputWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Writes payloads to stdout.
 * Defensive: init validation and robust write handling.
 */
public class ConsoleWriter implements OutputWriter {
    private static final Logger logger = Logger.getLogger(ConsoleWriter.class.getName());
    private String prefix = "";
    
    @Override
    public void init(Map<String, Object> parameters) throws Exception {
        if (parameters != null && parameters.get("prefix") != null) {
            prefix = parameters.get("prefix").toString();
        }
    }
    
    @Override
    public void write(Map<String, Object> payload) throws Exception {
        try {
            if (payload == null) {
                logger.warning("ConsoleWriter received null payload; skipping");
                return;
            }
            System.out.println(prefix + payload.toString());
        } catch (Exception e) {
            logger.log(Level.WARNING, "ConsoleWriter failed to write payload", e);
            throw e;
        }
    }
    
    @Override
    public String name() {
        return "ConsoleWriter";
    }
}