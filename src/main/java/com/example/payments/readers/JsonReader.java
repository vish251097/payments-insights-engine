package com.example.payments.readers;

import com.example.payments.engine.interfaces.DataReader;
import com.example.payments.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple JSON reader prototype.
 * Expects parameter: path -> path to JSON file/array of transactions
 *
 * Defensive I/O and parse handling.
 */
public class JsonReader implements DataReader {
    private static final Logger logger = Logger.getLogger(JsonReader.class.getName());
    private String path;
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public void init(Map<String, Object> parameters) throws Exception {
        if (parameters == null) throw new IllegalArgumentException("JsonReader requires parameters (path)");
        Object p = parameters.get("path");
        if (p == null) throw new IllegalArgumentException("JsonReader missing required parameter: path");
        path = p.toString();
        if (path.isBlank()) throw new IllegalArgumentException("JsonReader path must not be blank");
    }
    
    @Override
    public List<Transaction> readAll() throws Exception {
        File f = new File(path);
        if (!f.exists()) {
            logger.warning("JsonReader file does not exist: " + path);
            return new ArrayList<>();
        }
        try {
            // For safety we attempt to read an array of Transaction objects.
            Transaction[] arr = mapper.readValue(f, Transaction[].class);
            List<Transaction> list = new ArrayList<>();
            if (arr != null) {
                for (Transaction t : arr) if (t != null) list.add(t);
            }
            return list;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "JsonReader failed to parse file: " + path, e);
            throw e;
        }
    }
}