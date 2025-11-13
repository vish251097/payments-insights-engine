package com.example.payments.analytics;

import com.example.payments.engine.interfaces.Analytics;
import com.example.payments.model.Transaction;
import com.example.payments.utils.ErrorHandler;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Computes top merchants by value or volume.
 * Parameters: top_n (int), by: "value"|"volume"
 */
public class TopMerchantsAnalytics implements Analytics {

    private int topN = 5;
    private String by = "value";

    @Override
    public void init(Map<String, Object> parameters) throws Exception {
        try {
            if (parameters != null) {
                try {
                    if (parameters.containsKey("top_n"))
                        topN = Integer.parseInt(parameters.get("top_n").toString());
                } catch (Exception e) {
                    ErrorHandler.log("TopMerchantsAnalytics-Init-top_n", e);
                }

                try {
                    if (parameters.containsKey("by"))
                        by = parameters.get("by").toString();
                } catch (Exception e) {
                    ErrorHandler.log("TopMerchantsAnalytics-Init-by", e);
                }
            }
        } catch (Exception e) {
            ErrorHandler.log("TopMerchantsAnalytics-Init", e);
        }
    }

    @Override
    public Map<String, Object> analyze(List<Transaction> transactions) {

        Map<String, Object> out = new HashMap<>();

        try {
            if (transactions == null || transactions.isEmpty()) {
                System.err.println("TopMerchantsAnalytics: No transactions available.");
                return out;
            }

            // ============================
            // CASE 1 — Top Merchants by Volume
            // ============================
            if ("volume".equalsIgnoreCase(by)) {
                try {
                    Map<String, Long> counts = transactions.stream()
                            .filter(Objects::nonNull)
                            .filter(t -> t.getMerchantId() != null)
                            .collect(Collectors.groupingBy(
                                    Transaction::getMerchantId,
                                    Collectors.counting()
                            ));

                    List<Map.Entry<String, Long>> top = counts.entrySet().stream()
                            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                            .limit(topN)
                            .collect(Collectors.toList());

                    out.put("top_by_volume", top);

                } catch (Exception e) {
                    ErrorHandler.log("TopMerchantsAnalytics-Analyze-Volume", e);
                }

                return out;
            }

            // ============================
            // CASE 2 — Top Merchants by Value
            // ============================
            try {
                Map<String, BigDecimal> sums = new HashMap<>();

                for (Transaction t : transactions) {
                    if (t == null || t.getMerchantId() == null) continue;

                    BigDecimal amt = (t.getAmount() == null ? BigDecimal.ZERO : t.getAmount());

                    try {
                        sums.merge(t.getMerchantId(), amt, BigDecimal::add);
                    } catch (Exception mergeErr) {
                        ErrorHandler.log("TopMerchantsAnalytics-MergeValue-" + t.getMerchantId(), mergeErr);
                    }
                }

                List<Map.Entry<String, BigDecimal>> top = sums.entrySet().stream()
                        .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                        .limit(topN)
                        .collect(Collectors.toList());

                out.put("top_by_value", top);

            } catch (Exception e) {
                ErrorHandler.log("TopMerchantsAnalytics-Analyze-Value", e);
            }

        } catch (Exception e) {
            ErrorHandler.log("TopMerchantsAnalytics-Analyze", e);
        }

        return out;
    }

    @Override
    public String name() {
        return "TopMerchants";
    }
}
