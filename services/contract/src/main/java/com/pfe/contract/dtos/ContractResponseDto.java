package com.pfe.contract.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractResponseDto {
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String status;

    private String clientId;
    private String clientName;

    private String insurerId;
    private String insurerName;

    private String beneficiaryId;
    private String beneficiaryName;
}
