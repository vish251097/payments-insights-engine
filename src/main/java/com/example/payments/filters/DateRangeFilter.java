package com.example.payments.filters;

import com.example.payments.engine.interfaces.Filter;
import com.example.payments.model.Transaction;
import com.example.payments.utils.ErrorHandler;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Filters transactions to a date/time window.
 * Expects "from" and/or "to" in ISO_OFFSET_DATE_TIME format.
 */
public class DateRangeFilter implements Filter {

    private OffsetDateTime from;
    private OffsetDateTime to;

    @Override
    public void init(Map<String, Object> parameters) throws Exception {
        try {
            if (parameters == null) return;

            try {
                if (parameters.containsKey("from")) {
                    from = OffsetDateTime.parse(parameters.get("from").toString());
                }
            } catch (Exception e) {
                ErrorHandler.log("DateRangeFilter-Init-from", e);
            }

            try {
                if (parameters.containsKey("to")) {
                    to = OffsetDateTime.parse(parameters.get("to").toString());
                }
            } catch (Exception e) {
                ErrorHandler.log("DateRangeFilter-Init-to", e);
            }

        } catch (Exception e) {
            ErrorHandler.log("DateRangeFilter-Init", e);
        }
    }

    @Override
    public List<Transaction> apply(List<Transaction> input) {
        try {
            if (input == null || input.isEmpty()) return input;

            return input.stream()
                    .filter(t -> {
                        try {
                            if (t == null || t.getTimestamp() == null) return false;

                            if (from != null && t.getTimestamp().isBefore(from)) return false;
                            if (to != null && t.getTimestamp().isAfter(to)) return false;

                            return true;
                        } catch (Exception e) {
                            ErrorHandler.log("DateRangeFilter-Apply-Transaction", e);
                            return false; // skip faulty transaction
                        }
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            ErrorHandler.log("DateRangeFilter-Apply", e);
            return input; // fail-safe fallback: return original list
        }
    }

    @Override
    public String name() {
        return "DateRangeFilter";
    }
}
