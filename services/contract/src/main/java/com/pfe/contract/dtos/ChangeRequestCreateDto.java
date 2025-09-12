package com.pfe.contract.dtos;

import lombok.Data;

@Data
public class ChangeRequestCreateDto {
    private Long contractId;
    private String changeType;
    private String description; // requestedChanges
}




