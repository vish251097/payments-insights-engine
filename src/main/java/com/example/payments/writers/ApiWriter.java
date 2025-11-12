package com.example.payments.writers;

import com.example.payments.engine.interfaces.OutputWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * API writer placeholder.
 * Expects parameters like endpoint and apiKey in parameters map.
 * Defensive: validates configuration and logs errors. Actual HTTP client is out of scope for placeholder.
 */
public class ApiWriter implements OutputWriter {
    private static final Logger logger = Logger.getLogger(ApiWriter.class.getName());
    private String endpoint;
    private String apiKey;
    
    @Override
    public void init(Map<String, Object> parameters) throws Exception {
        if (parameters == null) throw new IllegalArgumentException("ApiWriter requires parameters (endpoint)");
        if (parameters.get("endpoint") == null) throw new IllegalArgumentException("ApiWriter missing required parameter: endpoint");
        endpoint = parameters.get("endpoint").toString();
        if (parameters.get("apiKey") != null) apiKey = parameters.get("apiKey").toString();
    }
    
    @Override
    public void write(Map<String, Object> payload) throws Exception {
        try {
            if (payload == null) {
                logger.warning("ApiWriter received null payload; skipping");
                return;
            }
            // Placeholder: in a real implementation send HTTP POST with serialized payload.
            logger.info("ApiWriter would POST to " + endpoint + " payload=" + payload.toString());
        } catch (Exception e) {
            logger.log(Level.WARNING, "ApiWriter failed to send payload to " + endpoint, e);
            throw e;
        }
    }
    
    @Override
    public String name() { return "ApiWriter"; }
}