package com.example.payments.readers;

import com.example.payments.engine.interfaces.DataReader;
import com.example.payments.model.Transaction;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;



import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * CSV reader using jackson-dataformat-csv.
 * Expects header row mapping to the CSV fields:
 * transactionId,timestamp,amount,paymentType,merchantId,merchantName,merchantCategory,customerId,location,status
 *
 * Note: this class may appear "unused" in the IDE because it's instantiated by reflection
 * from ComponentFactory / PluginRegistry. That's expected.
 */
@SuppressWarnings("unused")
public class CsvReader implements DataReader {
    private String path;
    private String dateFormat; // optional: "ISO_OFFSET_DATE_TIME" (default) or "ISO_LOCAL_DATE_TIME" etc.

    @Override
    public void init(Map<String, Object> parameters) throws Exception {
        this.path = (String) parameters.getOrDefault("path", "src/main/resources/sample/transactions.csv");
        this.dateFormat = (String) parameters.getOrDefault("date_format", "ISO_OFFSET_DATE_TIME");
    }

    @Override
    public List<Transaction> readAll() throws Exception {
        File csv = new File(path);
        if (!csv.exists()) {
            throw new IllegalArgumentException("CSV not found: " + path);
        }

        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        // Use a TypeReference so Jackson preserves generics (List<Map<String,String>>),
        // and use try-with-resources to close the MappingIterator.
        TypeReference<Map<String, String>> typeRef = new TypeReference<>() { };

        List<Map<String, String>> rows = new ArrayList<>();
        try (MappingIterator<Map<String, String>> it = mapper
                .readerFor(typeRef)
                .with(schema)
                .readValues(csv)) {
            // MappingIterator.readAll() will return a List<Map<String,String>>
            rows = it.readAll();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV: " + csv.getAbsolutePath(), e);
        }

        List<Transaction> out = new ArrayList<>();
        DateTimeFormatter isoOffset = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        DateTimeFormatter isoLocal  = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        // Allow dateFormat parameter to change parsing behaviour if desired
        boolean preferOffset = "ISO_OFFSET_DATE_TIME".equalsIgnoreCase(this.dateFormat);

        for (Map<String, String> r : rows) {
            Transaction t = new Transaction();
            // Support both new header names and previous names for backward compatibility
            t.setId(r.getOrDefault("transactionId", r.get("id")));
            t.setMerchantId(r.get("merchantId"));
            t.setMerchantName(r.get("merchantName"));
            t.setMerchantCategory(r.get("merchantCategory"));

            String amt = r.get("amount");
            t.setAmount(amt == null || amt.isBlank() ? BigDecimal.ZERO : new BigDecimal(amt));
            t.setCurrency(r.getOrDefault("currency", "INR"));
            t.setStatus(r.get("status"));

            String ts = r.get("timestamp");
            if (ts != null && !ts.isBlank()) {
                // Try OffsetDateTime first (if data has offset), otherwise parse LocalDateTime and convert
                if (preferOffset) {
                    try {
                        t.setTimestamp(OffsetDateTime.parse(ts, isoOffset));
                    } catch (Exception ex) {
                        // fallback to local parse
                        LocalDateTime ldt = LocalDateTime.parse(ts, isoLocal);
                        ZoneId zone = ZoneId.systemDefault();
                        t.setTimestamp(ldt.atZone(zone).toOffsetDateTime());
                    }
                } else {
                    try {
                        LocalDateTime ldt = LocalDateTime.parse(ts, isoLocal);
                        ZoneId zone = ZoneId.systemDefault();
                        t.setTimestamp(ldt.atZone(zone).toOffsetDateTime());
                    } catch (Exception ex) {
                        // fallback to parsing with offset if present
                        t.setTimestamp(OffsetDateTime.parse(ts, isoOffset));
                    }
                }
            }

            t.setPaymentType(r.get("paymentType")); // UPI/CARD/NETBANKING/WALLET
            t.setLocation(r.get("location"));
            t.setCustomerId(r.get("customerId"));

            out.add(t);
        }

        return out;
    }
}