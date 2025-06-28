package com.pfe.contract.dtos;

import lombok.Data;

@Data
public class ContractRequestDto {

    private String clientId;
    private String insurerId;
    private String beneficiaryId;

    private String creationDate;
    private String endDate;
    private String status;

    private Double montant;  // ✅ Nouveau champ ajouté
}
