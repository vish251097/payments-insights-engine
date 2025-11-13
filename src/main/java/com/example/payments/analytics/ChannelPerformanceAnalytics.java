package com.example.payments.analytics;

import com.example.payments.engine.interfaces.Analytics;
import com.example.payments.model.Transaction;
import com.example.payments.utils.ErrorHandler;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Aggregates performance by payment type (paymentType).
 * Produces counts and total value per paymentType.
 */
public class ChannelPerformanceAnalytics implements Analytics {

    @Override
    public void init(Map<String, Object> parameters) throws Exception { /* none for now */ }

    @Override
    public Map<String, Object> analyze(List<Transaction> transactions) {

        Map<String, Object> result = new HashMap<>();

        try {

            if (transactions == null || transactions.isEmpty()) {
                System.err.println("ChannelPerformanceAnalytics: No transactions to analyze.");
                result.put("counts", Collections.emptyMap());
                result.put("values", Collections.emptyMap());
                return result;
            }

            // 1. Counts per channel (safe)
            Map<String, Long> counts = new HashMap<>();
            try {
                counts = transactions.stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.groupingBy(
                                t -> nullToUnknown(t.getPaymentType()),
                                Collectors.counting()
                        ));
            } catch (Exception e) {
                ErrorHandler.log("ChannelPerformanceAnalytics-Counts", e);
            }

            // 2. Values per channel (safe)
            Map<String, BigDecimal> values = new HashMap<>();
            try {
                for (Transaction t : transactions) {
                    if (t == null) continue;

                    String ch = nullToUnknown(t.getPaymentType());
                    BigDecimal amt = t.getAmount() == null ? BigDecimal.ZERO : t.getAmount();

                    try {
                        values.merge(ch, amt, BigDecimal::add);
                    } catch (Exception inner) {
                        ErrorHandler.log("ChannelPerformanceAnalytics-MergeValue", inner);
                    }
                }
            } catch (Exception e) {
                ErrorHandler.log("ChannelPerformanceAnalytics-Values", e);
            }

            result.put("counts", counts);
            result.put("values", values);
            return result;

        } catch (Exception e) {
            ErrorHandler.log("ChannelPerformanceAnalytics", e);
            return result;
        }
    }

    private String nullToUnknown(String v) {
        return v == null ? "UNKNOWN" : v;
    }

    @Override
    public String name() {
        return "ChannelPerformance";
    }
}
