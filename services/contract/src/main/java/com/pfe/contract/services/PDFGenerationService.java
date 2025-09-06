package com.pfe.contract.services;

import com.pfe.contract.entities.Contract;

public interface PDFGenerationService {
    byte[] generateContractPDF(Contract contract);
    byte[] generateSignedContractPDF(Contract contract);
}
