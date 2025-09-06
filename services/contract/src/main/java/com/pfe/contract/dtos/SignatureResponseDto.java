package com.pfe.contract.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SignatureResponseDto {
    private Long contractId;
    private String clientSignature;
    private String insurerSignature;
    private LocalDate clientSignedAt;
    private LocalDate insurerSignedAt;
    private Boolean isFullySigned;
    private String message;
}
