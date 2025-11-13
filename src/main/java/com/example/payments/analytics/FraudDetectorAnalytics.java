package com.example.payments.analytics;

import com.example.payments.engine.interfaces.Analytics;
import com.example.payments.model.Transaction;
import com.example.payments.utils.ErrorHandler;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Basic fraud detector with:
 * - velocity check: transactions per customer in window (default 1h)
 * - amount anomaly: transactions over configured threshold
 */
public class FraudDetectorAnalytics implements Analytics {

    private Duration velocityWindow = Duration.ofHours(1);
    private int velocityThreshold = 10;
    private BigDecimal amountThreshold = BigDecimal.valueOf(10000);

    @Override
    public void init(Map<String, Object> parameters) throws Exception {
        try {
            if (parameters == null) return;

            if (parameters.containsKey("velocity_window")) {
                String v = parameters.get("velocity_window").toString();
                try {
                    if (v.startsWith("PT")) velocityWindow = Duration.parse(v);
                    else if (v.endsWith("h"))
                        velocityWindow = Duration.ofHours(Long.parseLong(v.replace("h", "")));
                } catch (Exception e) {
                    ErrorHandler.log("FraudDetectorAnalytics-Init-velocity_window", e);
                }
            }

            if (parameters.containsKey("velocity_threshold")) {
                try {
                    velocityThreshold = Integer.parseInt(parameters.get("velocity_threshold").toString());
                } catch (Exception e) {
                    ErrorHandler.log("FraudDetectorAnalytics-Init-velocity_threshold", e);
                }
            }

            if (parameters.containsKey("amount_threshold")) {
                try {
                    amountThreshold = new BigDecimal(parameters.get("amount_threshold").toString());
                } catch (Exception e) {
                    ErrorHandler.log("FraudDetectorAnalytics-Init-amount_threshold", e);
                }
            }

        } catch (Exception e) {
            ErrorHandler.log("FraudDetectorAnalytics-Init", e);
        }
    }

    @Override
    public Map<String, Object> analyze(List<Transaction> transactions) {

        Map<String, Object> out = new HashMap<>();

        try {

            if (transactions == null || transactions.isEmpty()) {
                System.err.println("FraudDetectorAnalytics: No transactions to analyze.");
                out.put("amount_anomalies", Collections.emptyList());
                out.put("velocity_alerts", Collections.emptyList());
                return out;
            }

            // ============================
            // 1. AMOUNT ANOMALIES
            // ============================
            List<Transaction> large = new ArrayList<>();
            try {
                large = transactions.stream()
                        .filter(Objects::nonNull)
                        .filter(t -> t.getAmount() != null)
                        .filter(t -> t.getAmount().compareTo(amountThreshold) > 0)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                ErrorHandler.log("FraudDetectorAnalytics-AmountAnomalies", e);
            }
            out.put("amount_anomalies", large);

            // ============================
            // 2. VELOCITY ALERTS
            // ============================
            List<Map<String, Object>> velocityAlerts = new ArrayList<>();

            try {

                Map<String, List<Transaction>> byCustomer =
                        transactions.stream()
                                .filter(Objects::nonNull)
                                .filter(t -> t.getCustomerId() != null && t.getTimestamp() != null)
                                .collect(Collectors.groupingBy(Transaction::getCustomerId));

                for (var e : byCustomer.entrySet()) {

                    try {
                        List<OffsetDateTime> times = e.getValue().stream()
                                .map(Transaction::getTimestamp)
                                .filter(Objects::nonNull)
                                .sorted()
                                .collect(Collectors.toList());

                        for (int i = 0; i < times.size(); i++) {
                            OffsetDateTime start = times.get(i);
                            int count = 0;

                            for (int j = i; j < times.size(); j++) {
                                if (!times.get(j).isAfter(start.plus(velocityWindow))) count++;
                                else break;
                            }

                            if (count >= velocityThreshold) {
                                Map<String, Object> alert = new HashMap<>();
                                alert.put("customerId", e.getKey());
                                alert.put("start", start);
                                alert.put("count", count);
                                velocityAlerts.add(alert);
                                break;
                            }
                        }
                    } catch (Exception inner) {
                        ErrorHandler.log("FraudDetectorAnalytics-Velocity-Customer-" + e.getKey(), inner);
                    }
                }

            } catch (Exception e) {
                ErrorHandler.log("FraudDetectorAnalytics-Velocity", e);
            }

            out.put("velocity_alerts", velocityAlerts);

        } catch (Exception e) {
            ErrorHandler.log("FraudDetectorAnalytics-Analyze", e);
        }

        return out;
    }

    @Override
    public String name() {
        return "FraudDetector";
    }
}
