package com.pfe.contract.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractResponseDto {
    private String id;
    private LocalDate creationDate;
    private LocalDate endDate;
    private String status;


    private String clientId;
    private String clientName;

    private String insurerId;
    private String insurerName;

    private String beneficiaryId;
    private String beneficiaryName;
}
