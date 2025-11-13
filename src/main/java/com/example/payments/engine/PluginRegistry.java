package com.example.payments.engine;

import com.example.payments.utils.ErrorHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Lightweight PluginRegistry mapping simple type names to implementation classes.
 * In a more advanced system this could be classpath scanning / service loader.
 */
public class PluginRegistry {

    private final Map<String, String> typeToClass = new HashMap<>();

    public PluginRegistry() {
        try {
            // default registrations

            // readers
            register("csv", "com.example.payments.readers.CsvReader");
            register("json", "com.example.payments.readers.JsonReader");
            register("parquet", "com.example.payments.readers.ParquetReader");

            // filters
            register("status_filter", "com.example.payments.filters.StatusFilter");
            register("date_range_filter", "com.example.payments.filters.DateRangeFilter");
            register("amount_filter", "com.example.payments.filters.AmountFilter");

            // analytics
            register("top_merchants", "com.example.payments.analytics.TopMerchantsAnalytics");
            register("channel_performance", "com.example.payments.analytics.ChannelPerformanceAnalytics");
            register("fraud_detection", "com.example.payments.analytics.FraudDetectorAnalytics");

            // writers
            register("console", "com.example.payments.writers.ConsoleWriter");
            register("file", "com.example.payments.writers.FileWriterImpl");
            register("api", "com.example.payments.writers.ApiWriter");

        } catch (Exception e) {
            ErrorHandler.log("PluginRegistry-Constructor", e);
        }
    }

    public void register(String typeName, String fqcn) {
        try {
            Objects.requireNonNull(typeName);
            Objects.requireNonNull(fqcn);
            typeToClass.put(typeName.toLowerCase(), fqcn);
        } catch (Exception e) {
            ErrorHandler.log("PluginRegistry-Register(" + typeName + ")", e);
        }
    }

    public String resolve(String typeName) {
        try {
            if (typeName == null) {
                ErrorHandler.log("PluginRegistry-Resolve", "Type name is null");
                return null;
            }
            return typeToClass.get(typeName.toLowerCase());
        } catch (Exception e) {
            ErrorHandler.log("PluginRegistry-Resolve(" + typeName + ")", e);
            return null;
        }
    }
}
