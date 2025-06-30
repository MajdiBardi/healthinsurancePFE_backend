package com.pfe.contract.dtos;

import lombok.Data;

@Data
public class ContractRequestDto {
    private String clientId;
    private String insurerId;
    private String beneficiaryId;

    private String duration;  // Ex: "6 mois", "1 an", "2 ans", etc.
    //private String status;

    private Double montant;
}
