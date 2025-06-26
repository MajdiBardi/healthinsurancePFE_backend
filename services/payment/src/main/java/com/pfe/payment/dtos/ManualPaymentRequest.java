package com.pfe.payment.dtos;

import com.pfe.payment.entities.TransactionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ManualPaymentRequest {
    private String contractId;
    private TransactionType type; // PAYMENT_CASH or PAYMENT_TRANSFER
    private BigDecimal amount;
}