package com.pfe.contract.controllers;

import com.pfe.contract.dtos.SignatureRequestDto;
import com.pfe.contract.dtos.SignatureResponseDto;
import com.pfe.contract.services.ElectronicSignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/signatures")
@RequiredArgsConstructor
public class ElectronicSignatureController {

    private final ElectronicSignatureService signatureService;

    @PostMapping("/sign")
    @PreAuthorize("hasAnyRole('CLIENT', 'INSURER')")
    public ResponseEntity<SignatureResponseDto> signContract(
            @RequestBody SignatureRequestDto request,
            Authentication authentication) {
        
        // Vérifier que l'utilisateur peut signer ce contrat
        String userId = authentication.getName();
        request.setSignerId(userId);
        
        // Déterminer le rôle du signataire
        String userRole = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENT")) ? "CLIENT" : "INSURER";
        request.setSignerRole(userRole);
        
        SignatureResponseDto response = signatureService.signContract(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/contract/{contractId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSURER', 'CLIENT')")
    public ResponseEntity<SignatureResponseDto> getSignatureStatus(@PathVariable Long contractId) {
        SignatureResponseDto response = signatureService.getContractSignatureStatus(contractId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{contractId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSURER', 'CLIENT')")
    public ResponseEntity<SignatureResponseDto> getContractSignatureStatus(@PathVariable Long contractId) {
        SignatureResponseDto response = signatureService.getContractSignatureStatus(contractId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/contract/{contractId}/verify")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSURER', 'CLIENT')")
    public ResponseEntity<Boolean> verifySignature(
            @PathVariable Long contractId,
            @RequestParam String signerRole) {
        boolean isValid = signatureService.verifySignature(contractId, signerRole);
        return ResponseEntity.ok(isValid);
    }
}
