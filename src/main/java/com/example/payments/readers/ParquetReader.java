package com.example.payments.readers;

import com.example.payments.engine.interfaces.DataReader;

/**
 * Placeholder for Parquet reader.
 */
public class ParquetReader implements DataReader {
    @Override
    public void init(java.util.Map<String, Object> parameters) throws Exception {
        // placeholder
    }

    @Override
    public java.util.List<com.example.payments.model.Transaction> readAll() throws Exception {
        throw new UnsupportedOperationException("ParquetReader not implemented in prototype");
    }
}