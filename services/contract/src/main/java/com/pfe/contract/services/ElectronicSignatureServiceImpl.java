package com.pfe.contract.services;

import com.pfe.contract.clients.NotificationClient;
import com.pfe.contract.dtos.NotificationEmailRequest;
import com.pfe.contract.dtos.SignatureRequestDto;
import com.pfe.contract.dtos.SignatureResponseDto;
import com.pfe.contract.entities.Contract;
import com.pfe.contract.repositories.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ElectronicSignatureServiceImpl implements ElectronicSignatureService {

    private final ContractRepository contractRepository;
    private final NotificationClient notificationClient;

    @Override
    public SignatureResponseDto signContract(SignatureRequestDto request) {
        System.out.println("=== SIGNATURE REQUEST ===");
        System.out.println("Contract ID: " + request.getContractId());
        System.out.println("Signer ID: " + request.getSignerId());
        System.out.println("Signer Role: " + request.getSignerRole());
        System.out.println("Signature length: " + (request.getSignature() != null ? request.getSignature().length() : "null"));
        System.out.println("Signature preview: " + (request.getSignature() != null ? request.getSignature().substring(0, Math.min(50, request.getSignature().length())) + "..." : "null"));
        
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        // Vérifier que la signature n'est pas vide
        if (request.getSignature() == null || request.getSignature().trim().isEmpty()) {
            throw new RuntimeException("Signature cannot be empty");
        }

        // Vérifier que la signature est valide (base64) - plus permissif
        if (!isValidBase64Signature(request.getSignature())) {
            System.out.println("Warning: Signature may not be valid base64, but proceeding anyway");
        }

        // Appliquer la signature selon le rôle
        System.out.println("🔍 Rôle reçu: " + request.getSignerRole());
        System.out.println("🔍 ID du signataire: " + request.getSignerId());
        
        if ("CLIENT".equals(request.getSignerRole())) {
            contract.setClientSignature(request.getSignature());
            contract.setClientSignedAt(LocalDate.now());
            System.out.println("✅ Signature CLIENT appliquée pour l'utilisateur: " + request.getSignerId());
        } else if ("INSURER".equals(request.getSignerRole())) {
            contract.setInsurerSignature(request.getSignature());
            contract.setInsurerSignedAt(LocalDate.now());
            System.out.println("✅ Signature INSURER appliquée pour l'utilisateur: " + request.getSignerId());
        } else {
            System.out.println("❌ Rôle invalide: " + request.getSignerRole());
            throw new RuntimeException("Invalid signer role: " + request.getSignerRole());
        }

        // Vérifier si le contrat est entièrement signé
        boolean isFullySigned = contract.getClientSignature() != null && 
                               contract.getInsurerSignature() != null;
        contract.setIsFullySigned(isFullySigned);
        
        System.out.println("📊 Statut des signatures:");
        System.out.println("  - Client signature: " + (contract.getClientSignature() != null ? "PRÉSENTE" : "ABSENTE"));
        System.out.println("  - Insurer signature: " + (contract.getInsurerSignature() != null ? "PRÉSENTE" : "ABSENTE"));
        System.out.println("  - Contrat entièrement signé: " + isFullySigned);

        Contract savedContract = contractRepository.save(contract);

        // Envoyer notification
        sendSignatureNotification(savedContract, request.getSignerRole());

        return mapToResponseDto(savedContract);
    }

    @Override
    public SignatureResponseDto getContractSignatureStatus(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        return mapToResponseDto(contract);
    }

    @Override
    public boolean verifySignature(Long contractId, String signerRole) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        if ("CLIENT".equals(signerRole)) {
            return contract.getClientSignature() != null && contract.getClientSignedAt() != null;
        } else if ("INSURER".equals(signerRole)) {
            return contract.getInsurerSignature() != null && contract.getInsurerSignedAt() != null;
        }

        return false;
    }

    private boolean isValidBase64Signature(String signature) {
        try {
            Base64.getDecoder().decode(signature);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void sendSignatureNotification(Contract contract, String signerRole) {
        try {
            NotificationEmailRequest email = new NotificationEmailRequest();
            
            if ("CLIENT".equals(signerRole)) {
                email.setTo("insurer@system.local");
                email.setSubject("Signature client reçue - Contrat #" + contract.getId());
                email.setBody("Le client a signé le contrat #" + contract.getId() + 
                            ". Le contrat est maintenant prêt pour votre signature.");
            } else {
                email.setTo("client@system.local");
                email.setSubject("Signature assureur reçue - Contrat #" + contract.getId());
                email.setBody("L'assureur a signé le contrat #" + contract.getId() + 
                            ". Le contrat est maintenant entièrement signé.");
            }
            
            notificationClient.sendEmail(email);
        } catch (Exception ignored) {
            // Ignorer les erreurs de notification
        }
    }

    private SignatureResponseDto mapToResponseDto(Contract contract) {
        SignatureResponseDto dto = new SignatureResponseDto();
        dto.setContractId(contract.getId());
        dto.setClientSignature(contract.getClientSignature());
        dto.setInsurerSignature(contract.getInsurerSignature());
        dto.setClientSignedAt(contract.getClientSignedAt());
        dto.setInsurerSignedAt(contract.getInsurerSignedAt());
        dto.setIsFullySigned(contract.getIsFullySigned());
        
        if (Boolean.TRUE.equals(contract.getIsFullySigned())) {
            dto.setMessage("Contrat entièrement signé");
        } else if (contract.getClientSignature() != null) {
            dto.setMessage("En attente de signature de l'assureur");
        } else {
            dto.setMessage("En attente de signature du client");
        }
        
        return dto;
    }
}
