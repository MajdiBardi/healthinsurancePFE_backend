package com.pfe.contract.services;

import com.pfe.contract.dtos.SignatureRequestDto;
import com.pfe.contract.dtos.SignatureResponseDto;

public interface ElectronicSignatureService {
    SignatureResponseDto signContract(SignatureRequestDto request);
    SignatureResponseDto getContractSignatureStatus(Long contractId);
    boolean verifySignature(Long contractId, String signerRole);
}
