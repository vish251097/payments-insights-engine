package com.example.payments.writers;

import com.example.payments.engine.interfaces.OutputWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Writes payloads to a file (appends).
 * Expects parameter: path
 *
 * Defensive: validates path and logs I/O errors.
 */
public class FileWriterImpl implements OutputWriter {
    private static final Logger logger = Logger.getLogger(FileWriterImpl.class.getName());
    private String path;
    
    @Override
    public void init(Map<String, Object> parameters) throws Exception {
        if (parameters == null || parameters.get("path") == null) {
            throw new IllegalArgumentException("FileWriterImpl requires parameter: path");
        }
        path = parameters.get("path").toString();
        if (path.isBlank()) throw new IllegalArgumentException("FileWriterImpl path must not be blank");
        File f = new File(path);
        try {
            File parent = f.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            if (!f.exists()) f.createNewFile();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize output file: " + path, e);
            throw e;
        }
    }
    
    @Override
    public synchronized void write(Map<String, Object> payload) throws Exception {
        Objects.requireNonNull(payload, "payload must not be null");
        try (FileWriter fw = new FileWriter(path, true)) {
            fw.write(payload.toString());
            fw.write(System.lineSeparator());
        } catch (Exception e) {
            logger.log(Level.WARNING, "FileWriterImpl failed to write payload to " + path, e);
            throw e;
        }
    }

    @Override
    public String name() { return "FileWriter"; }
}