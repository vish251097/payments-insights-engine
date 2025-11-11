package com.example.payments.engine.interfaces;

import com.example.payments.model.Transaction;

import java.util.List;
import java.util.Map;

/**
 * DataReader reads transactions from a configured source.
 * Implementations accept a parameter map from configuration.
 */
public interface DataReader {
    /**
     * Initialize the reader with config parameters.
     */
    void init(Map<String, Object> parameters) throws Exception;

    /**
     * Read all transactions (batch mode).
     */
    List<Transaction> readAll() throws Exception;
}