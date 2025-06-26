package com.pfe.payment.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymeePaymentRequest {
    private String contractId;
    private BigDecimal amount;
}