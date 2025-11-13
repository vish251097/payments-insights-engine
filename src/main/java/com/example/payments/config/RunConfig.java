package com.example.payments.config;

import java.util.List;
import java.util.Map;

public class RunConfig {
    public DataSourceConfig data_source;
    public List<FilterConfig> filters;
    public List<AnalyticsConfig> analytics;
    public OutputConfig output;

    public static class DataSourceConfig {
        public String type;
        public Map<String,Object> parameters;
    }

    public static class FilterConfig {
        public String type;
        public Map<String,Object> parameters;
    }

    public static class AnalyticsConfig {
        public String type;
        public Map<String,Object> parameters;
    }

    public static class OutputConfig {
        public String type;
        public Map<String,Object> parameters;
    }
}