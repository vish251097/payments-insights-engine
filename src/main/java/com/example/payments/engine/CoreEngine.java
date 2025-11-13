package com.example.payments.engine;

import com.example.payments.config.RunConfig;
import com.example.payments.engine.interfaces.Analytics;
import com.example.payments.engine.interfaces.DataReader;
import com.example.payments.engine.interfaces.Filter;
import com.example.payments.engine.interfaces.OutputWriter;
import com.example.payments.model.Transaction;
import com.example.payments.utils.ErrorHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CoreEngine orchestrates the pipeline:
 * 1. instantiate reader
 * 2. read transactions
 * 3. apply filters
 * 4. run analytics
 * 5. write outputs
 */
public class CoreEngine {
    private final PluginRegistry pluginRegistry = new PluginRegistry();
    private final ComponentFactory factory = new ComponentFactory(pluginRegistry);

    public void run(RunConfig config) {

        if (config == null) {
            ErrorHandler.log("CoreEngine", "RunConfig is null. Cannot start engine.");
            return;
        }

        List<Transaction> transactions = new ArrayList<>();
        List<Transaction> filtered = new ArrayList<>();
        List<Map<String,Object>> analyticsOutputs = new ArrayList<>();

        try {
            // 1. reader
            DataReader reader = null;
            try {
                reader = factory.createReader(config.data_source.type, config.data_source.parameters);
            } catch (Exception e) {
                ErrorHandler.log("CoreEngine-CreateReader", e);
            }

            if (reader != null) {
                try {
                    transactions = reader.readAll();
                } catch (Exception e) {
                    ErrorHandler.log("CoreEngine-ReadAll", e);
                    transactions = new ArrayList<>();
                }
            }

            // 2. filters
            filtered = transactions;
            if (config.filters != null) {
                for (var fc : config.filters) {
                    try {
                        Filter f = factory.createFilter(fc.type, fc.parameters);
                        filtered = f.apply(filtered);

                        System.out.println("Applied filter: " + f.name() + " -> remaining=" + filtered.size());

                    } catch (Exception e) {
                        ErrorHandler.log("CoreEngine-ApplyFilter-" + fc.type, e);
                    }
                }
            }

            // 3. analytics
            if (config.analytics != null) {
                for (var ac : config.analytics) {
                    try {
                        Analytics a = factory.createAnalytics(ac.type, ac.parameters);
                        Map<String,Object> result = a.analyze(filtered);

                        Map<String,Object> payload = new HashMap<>();
                        payload.put("module", a.name());
                        payload.put("result", result);

                        analyticsOutputs.add(payload);

                    } catch (Exception e) {
                        ErrorHandler.log("CoreEngine-Analytics-" + ac.type, e);
                    }
                }
            }

            // 4. writers
            if (config.output != null) {
                try {
                    OutputWriter writer = factory.createWriter(config.output.type, config.output.parameters);

                    for (Map<String,Object> payload : analyticsOutputs) {
                        try {
                            writer.write(payload);
                        } catch (Exception e) {
                            ErrorHandler.log("CoreEngine-WriterWrite", e);
                        }
                    }

                    System.out.println("Wrote " + analyticsOutputs.size() +
                            " analytics payloads via " + writer.name());

                } catch (Exception e) {
                    ErrorHandler.log("CoreEngine-CreateWriter", e);
                }
            }

        } catch (Exception e) {
            ErrorHandler.log("CoreEngine-RunUnexpected", e);
        }
    }
}