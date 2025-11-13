package com.example.payments.engine;

import com.example.payments.engine.interfaces.Analytics;
import com.example.payments.engine.interfaces.DataReader;
import com.example.payments.engine.interfaces.Filter;
import com.example.payments.engine.interfaces.OutputWriter;
import com.example.payments.utils.ErrorHandler;

import java.util.Map;

/**
 * ComponentFactory creates instances of readers/filters/analytics/writers
 * using the PluginRegistry + reflection. Instances are initialized with provided parameters.
 */
public class ComponentFactory {

    private final PluginRegistry registry;

    public ComponentFactory(PluginRegistry registry) {
        this.registry = registry;
    }

    @SuppressWarnings("unchecked")
    public DataReader createReader(String type, Map<String, Object> params) {
        try {
            String fqcn = registry.resolve(type);
            if (fqcn == null) {
                ErrorHandler.log("ComponentFactory-Reader", "Unknown reader type: " + type);
                return null;
            }

            Class<?> c = Class.forName(fqcn);
            DataReader inst = (DataReader) c.getDeclaredConstructor().newInstance();
            inst.init(params == null ? Map.of() : params);
            return inst;

        } catch (Exception e) {
            ErrorHandler.log("ComponentFactory-CreateReader(" + type + ")", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Filter createFilter(String type, Map<String, Object> params) {
        try {
            String fqcn = registry.resolve(type);
            if (fqcn == null) {
                ErrorHandler.log("ComponentFactory-Filter", "Unknown filter type: " + type);
                return null;
            }

            Class<?> c = Class.forName(fqcn);
            Filter inst = (Filter) c.getDeclaredConstructor().newInstance();
            inst.init(params == null ? Map.of() : params);
            return inst;

        } catch (Exception e) {
            ErrorHandler.log("ComponentFactory-CreateFilter(" + type + ")", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Analytics createAnalytics(String type, Map<String, Object> params) {
        try {
            String fqcn = registry.resolve(type);
            if (fqcn == null) {
                ErrorHandler.log("ComponentFactory-Analytics", "Unknown analytics type: " + type);
                return null;
            }

            Class<?> c = Class.forName(fqcn);
            Analytics inst = (Analytics) c.getDeclaredConstructor().newInstance();
            inst.init(params == null ? Map.of() : params);
            return inst;

        } catch (Exception e) {
            ErrorHandler.log("ComponentFactory-CreateAnalytics(" + type + ")", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public OutputWriter createWriter(String type, Map<String, Object> params) {
        try {
            String fqcn = registry.resolve(type);
            if (fqcn == null) {
                ErrorHandler.log("ComponentFactory-Writer", "Unknown writer type: " + type);
                return null;
            }

            Class<?> c = Class.forName(fqcn);
            OutputWriter inst = (OutputWriter) c.getDeclaredConstructor().newInstance();
            inst.init(params == null ? Map.of() : params);
            return inst;

        } catch (Exception e) {
            ErrorHandler.log("ComponentFactory-CreateWriter(" + type + ")", e);
            return null;
        }
    }
}
