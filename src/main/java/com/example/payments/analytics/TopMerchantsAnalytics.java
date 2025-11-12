package com.example.payments.analytics;

import com.example.payments.engine.interfaces.Analytics;
import com.example.payments.model.Transaction;
import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Computes top merchants by value or volume.
 * Parameters: top_n (int), by: "value"|"volume"
 *
 * Defensive: parameter validation, null-safe analyze and error return.
 */
public class TopMerchantsAnalytics implements Analytics {
    private static final Logger logger = Logger.getLogger(TopMerchantsAnalytics.class.getName());
    private int topN = 5;
    private String by = "value";
    
    @Override
    public void init(Map<String, Object> parameters) throws Exception {
        if (parameters == null) return;
        try {
            if (parameters.containsKey("top_n") && parameters.get("top_n") != null) topN = Integer.parseInt(parameters.get("top_n").toString());
            if (parameters.containsKey("by") && parameters.get("by") != null) by = parameters.get("by").toString();
            if (topN <= 0) topN = 5;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid parameters for TopMerchantsAnalytics: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Object> analyze(List<Transaction> transactions) {
        Map<String, Object> out = new HashMap<>();
        try {
            List<Transaction> txs = transactions == null ? List.of() : transactions.stream().filter(Objects::nonNull).collect(Collectors.toList());
            if ("volume".equalsIgnoreCase(by)) {
                Map<String, Long> counts = txs.stream()
                        .collect(Collectors.groupingBy(Transaction::getMerchantId, Collectors.counting()));
                List<Map.Entry<String, Long>> top = counts.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(topN)
                        .collect(Collectors.toList());
                out.put("top_by_volume", top);
            } else {
                // by value
                Map<String, BigDecimal> sums = new HashMap<>();
                for (Transaction t : txs) {
                    String merchant = t.getMerchantId();
                    if (merchant == null) continue;
                    sums.merge(merchant, t.getAmount() == null ? BigDecimal.ZERO : t.getAmount(), BigDecimal::add);
                }
                List<Map.Entry<String, BigDecimal>> top = sums.entrySet().stream()
                        .sorted((a,b)-> b.getValue().compareTo(a.getValue()))
                        .limit(topN)
                        .collect(Collectors.toList());
                out.put("top_by_value", top);
            }
            return out;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "TopMerchantsAnalytics failed during analyze", e);
            out.put("error", "TopMerchantsAnalytics analyze failed: " + e.toString());
            out.put("top_by_value", List.of());
            out.put("top_by_volume", List.of());
            return out;
        }
    }
    
    @Override
    public String name() { return "TopMerchants"; }
}