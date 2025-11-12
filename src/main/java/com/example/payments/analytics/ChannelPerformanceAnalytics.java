package com.example.payments.analytics;

import com.example.payments.engine.interfaces.Analytics;
import com.example.payments.model.Transaction;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Aggregates performance by payment channel.
 * Produces counts and total value per channel.
 *
 * Defensive: safe handling of nulls and exceptions during analysis.
 */
public class ChannelPerformanceAnalytics implements Analytics {
    private static final Logger logger = Logger.getLogger(ChannelPerformanceAnalytics.class.getName());

    @Override
    public void init(Map<String, Object> parameters) throws Exception { /* none for now */ }

    @Override
    public Map<String, Object> analyze(List<Transaction> transactions) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (transactions == null) {
                result.put("counts", Map.of());
                result.put("values", Map.of());
                return result;
            }

            Map<String, Long> counts = transactions.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(t -> nullToUnknown(t.getChannel()), Collectors.counting()));

            Map<String, BigDecimal> values = new HashMap<>();
            for (Transaction t : transactions) {
                if (t == null) continue;
                String ch = nullToUnknown(t.getChannel());
                BigDecimal amt = t.getAmount() == null ? BigDecimal.ZERO : t.getAmount();
                values.merge(ch, amt, BigDecimal::add);
            }

            result.put("counts", counts);
            result.put("values", values);
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "ChannelPerformanceAnalytics failed during analyze", e);
            result.put("error", "ChannelPerformanceAnalytics analyze failed: " + e.toString());
            result.put("counts", Map.of());
            result.put("values", Map.of());
            return result;
        }
    }

    private String nullToUnknown(String v) { return v == null ? "UNKNOWN" : v; }

    @Override
    public String name() { return "ChannelPerformance"; }
}