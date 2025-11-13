package com.example.payments.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Transaction domain object (POJO).
 * Fields map to CSV columns in the provided data.
 */
public class Transaction {
    // keep simple id name used elsewhere in the code
    private String id; // maps to CSV transactionId
    private String merchantId;
    private String merchantName;
    private String merchantCategory; // new field
    private BigDecimal amount;
    private String currency;
    private String status;
    private OffsetDateTime timestamp;
    private String paymentType; // maps to CSV paymentType (UPI/CARD/NETBANKING/WALLET)
    private String location; // maps to CSV location (city)
    private String customerId;

    public Transaction() {}

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

    public String getMerchantCategory() { return merchantCategory; }
    public void setMerchantCategory(String merchantCategory) { this.merchantCategory = merchantCategory; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", merchantCategory='" + merchantCategory + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", timestamp=" + timestamp +
                ", paymentType='" + paymentType + '\'' +
                ", location='" + location + '\'' +
                ", customerId='" + customerId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}