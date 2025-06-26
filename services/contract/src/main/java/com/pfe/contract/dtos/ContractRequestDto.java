package com.pfe.contract.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractRequestDto {
    private String clientId;
    private String insurerId;
    private String beneficiaryId;
    private LocalDate creationDate;
    private LocalDate endDate;
    private String status;

}
