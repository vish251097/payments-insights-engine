package com.example.payments.readers;

import com.example.payments.engine.interfaces.DataReader;
import com.example.payments.model.Transaction;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple CSV reader prototype.
 * Expects parameter: path -> path to CSV file
 *
 * Defensive: handles IO errors and malformed lines gracefully by logging and skipping.
 */
public class CsvReader implements DataReader {
    private static final Logger logger = Logger.getLogger(CsvReader.class.getName());
    private String path;
    
    @Override
    public void init(Map<String, Object> parameters) throws Exception {
        if (parameters == null) throw new IllegalArgumentException("CsvReader requires parameters (path)");
        Object p = parameters.get("path");
        if (p == null) throw new IllegalArgumentException("CsvReader missing required parameter: path");
        path = p.toString();
        if (path.isBlank()) throw new IllegalArgumentException("CsvReader path must not be blank");
    }
    
    @Override
    public List<Transaction> readAll() throws Exception {
        List<Transaction> out = new ArrayList<>();
        File f = new File(path);
        if (!f.exists()) {
            logger.warning("CsvReader file does not exist: " + path);
            return out;
        }
        try (BufferedReader br = Files.newBufferedReader(f.toPath())) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    // The parsing logic is application-specific; here we skip parsing for brevity.
                    // In a real implementation, parse CSV into Transaction objects.
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Skipping malformed CSV line: " + line, e);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to read CSV file: " + path, e);
            throw e;
        }
        return out;
    }
}