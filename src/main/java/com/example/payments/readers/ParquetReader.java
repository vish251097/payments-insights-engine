package com.example.payments.readers;

import com.example.payments.engine.interfaces.DataReader;
import com.example.payments.model.Transaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Parquet reader placeholder.
 * Real implementation requires parquet libraries; this placeholder returns empty list and logs.
 *
 * Defensive: initialization validated; readAll returns empty list instead of throwing in absence of real implementation.
 */
public class ParquetReader implements DataReader {
    private static final Logger logger = Logger.getLogger(ParquetReader.class.getName());
    private String path;
    
    @Override
    public void init(Map<String, Object> parameters) throws Exception {
        if (parameters == null || parameters.get("path") == null) {
            throw new IllegalArgumentException("ParquetReader requires parameter: path");
        }
        path = parameters.get("path").toString();
    }
    
    @Override
    public List<Transaction> readAll() throws Exception {
        logger.warning("ParquetReader is a placeholder; no parquet implementation available. path=" + path);
        return new ArrayList<>();
    }
}