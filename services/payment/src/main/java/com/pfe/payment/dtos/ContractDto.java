package com.pfe.payment.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractDto {
    private String id;
    private String status;
    private String performanceMetrics;
    private String clientId;
    private String insurerId;
    private LocalDate creationDate;
    private LocalDate endDate;
}