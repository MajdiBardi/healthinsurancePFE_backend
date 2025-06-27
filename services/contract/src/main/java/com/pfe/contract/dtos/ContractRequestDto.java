package com.pfe.contract.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractRequestDto {

    private String clientId;
    private String insurerId;
    private String beneficiaryId;

    //@JsonFormat(pattern = "yyyy-MM-dd")
    private String creationDate;

    //@JsonFormat(pattern = "yyyy-MM-dd")
    private String endDate;

    private String status;
}
