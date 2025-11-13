package com.example.payments.filters;

import com.example.payments.engine.interfaces.Filter;
import com.example.payments.model.Transaction;
import com.example.payments.utils.ErrorHandler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Keeps only transactions whose status is in configured allowed_statuses (List<String>).
 */
public class StatusFilter implements Filter {

    private Set<String> allowed;

    @Override
    public void init(Map<String, Object> parameters) throws Exception {
        try {
            if (parameters == null) {
                allowed = Set.of("COMPLETED");
                return;
            }

            try {
                Object v = parameters.get("parameters"); // e.g. ["COMPLETED"]
                if (v instanceof List) {
                    allowed = ((List<?>) v)
                            .stream()
                            .map(Object::toString)
                            .collect(Collectors.toSet());
                    return;
                }
            } catch (Exception e) {
                ErrorHandler.log("StatusFilter-Init-parameters", e);
            }

            try {
                if (parameters.containsKey("allowed_statuses")) {
                    allowed = ((List<?>) parameters.get("allowed_statuses"))
                            .stream()
                            .map(Object::toString)
                            .collect(Collectors.toSet());
                    return;
                }
            } catch (Exception e) {
                ErrorHandler.log("StatusFilter-Init-allowed_statuses", e);
            }

            // fallback default
            allowed = Set.of("COMPLETED");

        } catch (Exception e) {
            ErrorHandler.log("StatusFilter-Init", e);
            allowed = Set.of("COMPLETED");
        }
    }

    @Override
    public List<Transaction> apply(List<Transaction> input) {
        try {

            if (allowed == null || allowed.isEmpty() || input == null) return input;

            return input.stream()
                    .filter(t -> {
                        try {
                            return t != null &&
                                    t.getStatus() != null &&
                                    allowed.contains(t.getStatus());
                        } catch (Exception e) {
                            ErrorHandler.log("StatusFilter-Apply-Transaction", e);
                            return false;
                        }
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            ErrorHandler.log("StatusFilter-Apply", e);
            return input; // fail-safe fallback
        }
    }

    @Override
    public String name() {
        return "StatusFilter";
    }
}
