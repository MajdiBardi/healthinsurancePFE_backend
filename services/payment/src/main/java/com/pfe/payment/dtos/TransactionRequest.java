package com.pfe.payment.dtos;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private String contractId;
    private String type; // "DEPOSIT", "WITHDRAWAL", etc.
    private BigDecimal amount;
    private String userId;
}