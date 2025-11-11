package com.example.payments.engine.interfaces;

import com.example.payments.model.Transaction;

import java.util.List;
import java.util.Map;

/**
 * Analytics module processes transactions and returns a result payload.
 */
public interface Analytics {
    void init(Map<String, Object> parameters) throws Exception;
    /**
     * returns a map containing result data which is writer-serializable.
     */
    Map<String, Object> analyze(List<Transaction> transactions);
    String name();
}