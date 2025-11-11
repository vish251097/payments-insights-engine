package com.example.payments.engine.interfaces;

import com.example.payments.model.Transaction;

import java.util.List;
import java.util.Map;

/**
 * Filter applies filtering to a list of transactions.
 */
public interface Filter {
    void init(Map<String, Object> parameters) throws Exception;
    List<Transaction> apply(List<Transaction> input);
    String name();
}
