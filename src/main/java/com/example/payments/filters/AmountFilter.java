package com.example.payments.filters;

import com.example.payments.engine.interfaces.Filter;
import com.example.payments.model.Transaction;
import com.example.payments.utils.ErrorHandler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Keeps only transactions with amount within bounds (min_amount, max_amount).
 */
public class AmountFilter implements Filter {

    private BigDecimal min;
    private BigDecimal max;

    @Override
    public void init(Map<String, Object> parameters) throws Exception {
        try {
            if (parameters == null) return;

            try {
                if (parameters.containsKey("min_amount")) {
                    min = new BigDecimal(parameters.get("min_amount").toString());
                }
            } catch (Exception e) {
                ErrorHandler.log("AmountFilter-Init-min_amount", e);
            }

            try {
                if (parameters.containsKey("max_amount")) {
                    max = new BigDecimal(parameters.get("max_amount").toString());
                }
            } catch (Exception e) {
                ErrorHandler.log("AmountFilter-Init-max_amount", e);
            }

        } catch (Exception e) {
            ErrorHandler.log("AmountFilter-Init", e);
        }
    }

    @Override
    public List<Transaction> apply(List<Transaction> input) {
        try {

            if (input == null || input.isEmpty()) return input;

            return input.stream()
                    .filter(t -> {
                        try {
                            if (t == null || t.getAmount() == null) return false;

                            if (min != null && t.getAmount().compareTo(min) < 0) return false;
                            if (max != null && t.getAmount().compareTo(max) > 0) return false;

                            return true;

                        } catch (Exception e) {
                            ErrorHandler.log("AmountFilter-Apply-Transaction", e);
                            return false; // fail-safe: skip faulty transaction
                        }
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            ErrorHandler.log("AmountFilter-Apply", e);
            return input; // fail-safe: return original list
        }
    }

    @Override
    public String name() {
        return "AmountFilter";
    }
}
