package com.pfe.contract.dtos;

import lombok.Data;

@Data
public class SignatureRequestDto {
    private Long contractId;
    private String signature; // Signature en base64
    private String signerId; // ID de la personne qui signe
    private String signerRole; // CLIENT ou INSURER
}
