package com.pfe.contract.dtos;

import lombok.Data;

@Data
public class TransactionRequest {
    private String contractId;
    private Double amount;
    private String type; // e.g., "DEPOSIT", "WITHDRAWAL"
    private String userId;
}