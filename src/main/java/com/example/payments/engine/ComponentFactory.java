package com.example.payments.engine;

import com.example.payments.engine.interfaces.Analytics;
import com.example.payments.engine.interfaces.DataReader;
import com.example.payments.engine.interfaces.Filter;
import com.example.payments.engine.interfaces.OutputWriter;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ComponentFactory creates instances of readers/filters/analytics/writers
 * using the PluginRegistry + reflection. Instances are initialized with provided parameters.
 *
 * Added defensive exception handling and logging so failures include context and causes.
 */
public class ComponentFactory {
    private static final Logger logger = Logger.getLogger(ComponentFactory.class.getName());
    private final PluginRegistry registry;

    public ComponentFactory(PluginRegistry registry) {
        this.registry = registry;
    }

    @SuppressWarnings("unchecked")
    public DataReader createReader(String type, Map<String,Object> params) throws Exception {
        String fqcn = registry.resolve(type);
        if (fqcn == null) {
            throw new IllegalArgumentException("Unknown reader type: " + type);
        }
        try {
            Class<?> c = Class.forName(fqcn);
            DataReader inst = (DataReader) c.getDeclaredConstructor().newInstance();
            inst.init(params == null ? Map.of() : params);
            return inst;
        } catch (ClassNotFoundException cnf) {
            logger.log(Level.SEVERE, "Reader class not found for type=" + type + ", fqcn=" + fqcn, cnf);
            throw cnf;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create/initialize reader type=" + type + ", fqcn=" + fqcn, e);
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public Filter createFilter(String type, Map<String,Object> params) throws Exception {
        String fqcn = registry.resolve(type);
        if (fqcn == null) {
            throw new IllegalArgumentException("Unknown filter type: " + type);
        }
        try {
            Class<?> c = Class.forName(fqcn);
            Filter inst = (Filter) c.getDeclaredConstructor().newInstance();
            inst.init(params == null ? Map.of() : params);
            return inst;
        } catch (ClassNotFoundException cnf) {
            logger.log(Level.SEVERE, "Filter class not found for type=" + type + ", fqcn=" + fqcn, cnf);
            throw cnf;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create/initialize filter type=" + type + ", fqcn=" + fqcn, e);
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public Analytics createAnalytics(String type, Map<String,Object> params) throws Exception {
        String fqcn = registry.resolve(type);
        if (fqcn == null) {
            throw new IllegalArgumentException("Unknown analytics type: " + type);
        }
        try {
            Class<?> c = Class.forName(fqcn);
            Analytics inst = (Analytics) c.getDeclaredConstructor().newInstance();
            inst.init(params == null ? Map.of() : params);
            return inst;
        } catch (ClassNotFoundException cnf) {
            logger.log(Level.SEVERE, "Analytics class not found for type=" + type + ", fqcn=" + fqcn, cnf);
            throw cnf;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create/initialize analytics type=" + type + ", fqcn=" + fqcn, e);
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public OutputWriter createWriter(String type, Map<String,Object> params) throws Exception {
        String fqcn = registry.resolve(type);
        if (fqcn == null) {
            throw new IllegalArgumentException("Unknown writer type: " + type);
        }
        try {
            Class<?> c = Class.forName(fqcn);
            OutputWriter inst = (OutputWriter) c.getDeclaredConstructor().newInstance();
            inst.init(params == null ? Map.of() : params);
            return inst;
        } catch (ClassNotFoundException cnf) {
            logger.log(Level.SEVERE, "Writer class not found for type=" + type + ", fqcn=" + fqcn, cnf);
            throw cnf;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to create/initialize writer type=" + type + ", fqcn=" + fqcn, e);
            throw e;
        }
    }
}