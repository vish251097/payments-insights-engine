package com.example.payments.writers;

import com.example.payments.engine.interfaces.OutputWriter;

import java.util.Map;

/**
 * Stub for REST API writer. In a real system, use Spring RestTemplate / WebClient.
 */
public class ApiWriter implements OutputWriter {
    private String endpoint;

    @Override
    public void init(Map<String, Object> parameters) throws Exception {
        this.endpoint = parameters != null ? String.valueOf(parameters.get("endpoint")) : null;
    }

    @Override
    public void write(Map<String, Object> payload) throws Exception {
        // Prototype stub
        System.out.println("ApiWriter stub - would POST to " + endpoint + " payload=" + payload.get("module"));
    }

    @Override
    public String name() { return "ApiWriter"; }
}