package com.example.payments.analytics;

import com.example.payments.engine.interfaces.Analytics;
import com.example.payments.model.Transaction;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Basic fraud detector with:
 * - velocity check: transactions per customer in window (default 1h)
 * - amount anomaly: transactions over configured threshold
 *
 * Defensive: validate init params, guard nulls, catch runtime errors in analyze.
 */
public class FraudDetectorAnalytics implements Analytics {
    private static final Logger logger = Logger.getLogger(FraudDetectorAnalytics.class.getName());
    private Duration velocityWindow = Duration.ofHours(1);
    private int velocityThreshold = 10;
    private BigDecimal amountThreshold = BigDecimal.valueOf(10000);
    
    @Override
    public void init(Map<String, Object> parameters) throws Exception {
        if (parameters == null) return;
        try {
            if (parameters.containsKey("velocity_window") && parameters.get("velocity_window") != null) {
                String v = parameters.get("velocity_window").toString();
                if (v.startsWith("PT")) velocityWindow = Duration.parse(v);
                else if (v.endsWith("h")) velocityWindow = Duration.ofHours(Long.parseLong(v.replace("h","")));
                else throw new IllegalArgumentException("Unsupported velocity_window format: " + v);
            }
            if (parameters.containsKey("velocity_threshold") && parameters.get("velocity_threshold") != null) {
                velocityThreshold = Integer.parseInt(parameters.get("velocity_threshold").toString());
            }
            if (parameters.containsKey("amount_threshold") && parameters.get("amount_threshold") != null) {
                amountThreshold = new BigDecimal(parameters.get("amount_threshold").toString());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid parameters for FraudDetectorAnalytics: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Object> analyze(List<Transaction> transactions) {
        Map<String, Object> out = new HashMap<>();
        try {
            List<Transaction> txs = transactions == null ? List.of() : transactions.stream().filter(Objects::nonNull).collect(Collectors.toList());
            // amount anomalies
            List<Transaction> large = txs.stream()
                    .filter(t -> t.getAmount() != null && t.getAmount().compareTo(amountThreshold) > 0)
                    .collect(Collectors.toList());
            out.put("amount_anomalies", large);
            // velocity: group by customer and sliding-window count (naive O(n^2) for prototype)
            Map<String, List<Transaction>> byCustomer = txs.stream()
                    .filter(t -> t.getCustomerId() != null && t.getTimestamp() != null)
                    .collect(Collectors.groupingBy(Transaction::getCustomerId));
            List<Map<String,Object>> velocityAlerts = new ArrayList<>();
            for (var e : byCustomer.entrySet()) {
                List<OffsetDateTime> times = e.getValue().stream()
                        .map(Transaction::getTimestamp)
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
                        Map<String,Object> alert = new HashMap<>();
                        alert.put("customerId", e.getKey());
                        alert.put("start", start);
                        alert.put("count", count);
                        velocityAlerts.add(alert);
                        break;
                    }
                }
            }
            out.put("velocity_alerts", velocityAlerts);
            return out;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "FraudDetectorAnalytics failed during analyze", e);
            out.put("error", "FraudDetectorAnalytics analyze failed: " + e.toString());
            out.put("amount_anomalies", List.of());
            out.put("velocity_alerts", List.of());
            return out;
        }
    }
    
    @Override
    public String name() { return "FraudDetector"; }
}