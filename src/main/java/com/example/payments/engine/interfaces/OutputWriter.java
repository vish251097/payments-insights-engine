package com.example.payments.engine.interfaces;

import java.util.Map;

/**
 * OutputWriter writes analytics results to destinations.
 */
public interface OutputWriter {
    void init(Map<String, Object> parameters) throws Exception;
    void write(Map<String, Object> payload) throws Exception;
    String name();
}