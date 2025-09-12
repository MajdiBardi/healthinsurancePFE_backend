package com.pfe.contract.controllers;

import com.pfe.contract.dtos.ContractRequestDto;
import com.pfe.contract.dtos.ContractResponseDto;
import com.pfe.contract.entities.Contract;
import com.pfe.contract.services.ContractService;
import com.pfe.contract.services.PDFGenerationService;
import com.pfe.contract.services.ElectronicSignatureService;
import com.pfe.contract.dtos.SignatureRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final PDFGenerationService pdfGenerationService;
    private final ElectronicSignatureService electronicSignatureService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSURER')")
    public ResponseEntity<ContractResponseDto> createContract(@RequestBody ContractRequestDto dto) {
        Contract contract = mapDtoToEntity(dto);
        Contract created = contractService.createContract(contract);
        return ResponseEntity.ok(mapEntityToDto(created));
    }

    @GetMapping("/{id}/details")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSURER')")
    public ResponseEntity<ContractResponseDto> getContractDetails(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getContractDetails(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSURER')")
    public ResponseEntity<Contract> updateContract(@PathVariable Long id, @RequestBody ContractRequestDto dto) {
        Contract contract = mapDtoToEntity(dto);
        return ResponseEntity.ok(contractService.updateContract(id, contract));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSURER')")
    public ResponseEntity<Contract> getContract(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getContractById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSURER')")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSURER')")
    public ResponseEntity<List<Contract>> getAllContracts() {
        return ResponseEntity.ok(contractService.getAllContracts());
    }

    @GetMapping("/my-contracts")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<Contract>> getMyContracts(Authentication authentication) {
        String userId = authentication.getName();
        return ResponseEntity.ok(contractService.getContractsByClientId(userId));
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSURER', 'CLIENT')")
    public ResponseEntity<byte[]> downloadContractPDF(@PathVariable Long id) {
        Contract contract = contractService.getContractById(id);
        byte[] pdfBytes = pdfGenerationService.generateSignedContractPDF(contract);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "contrat_" + id + ".pdf");
        headers.setContentLength(pdfBytes.length);
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/{id}/signature-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSURER', 'CLIENT')")
    public ResponseEntity<?> getContractSignatureStatus(@PathVariable Long id) {
        try {
            var response = electronicSignatureService.getContractSignatureStatus(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/sign")
    @PreAuthorize("hasAnyRole('CLIENT', 'INSURER')")
    public ResponseEntity<?> signContract(@PathVariable Long id, @RequestBody Map<String, Object> requestBody, Authentication authentication) {
        try {
            System.out.println("=== CONTRACT CONTROLLER SIGN ===");
            System.out.println("Contract ID from path: " + id);
            System.out.println("Request body: " + requestBody);
            System.out.println("Authentication name: " + authentication.getName());
            System.out.println("Authentication authorities: " + authentication.getAuthorities());
            
            // Créer le DTO de signature
            SignatureRequestDto request = new SignatureRequestDto();
            request.setContractId(id);
            request.setSignerId(authentication.getName());
            request.setSignature((String) requestBody.get("signature"));
            
            // Utiliser le rôle envoyé par le frontend
            String userRole = (String) requestBody.get("userRole");
            if (userRole == null || userRole.isEmpty()) {
                // Fallback: déterminer le rôle automatiquement
                if (authentication.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_CLIENT"))) {
                    userRole = "CLIENT";
                } else if (authentication.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_INSURER"))) {
                    userRole = "INSURER";
                } else if (authentication.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                    userRole = "INSURER"; // Admin peut signer comme assureur
                } else {
                    userRole = "UNKNOWN";
                }
            }
            
            System.out.println("Rôle utilisé: " + userRole + " (envoyé par frontend: " + requestBody.get("userRole") + ")");
            request.setSignerRole(userRole);
            
            System.out.println("Final request: " + request);
            
            var response = electronicSignatureService.signContract(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error in contract controller: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erreur lors de la signature: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/test-signature")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSURER', 'CLIENT')")
    public ResponseEntity<?> testSignature(@PathVariable Long id) {
        try {
            Contract contract = contractService.getContractById(id);
            return ResponseEntity.ok(Map.of(
                "contractId", contract.getId(),
                "clientSignature", contract.getClientSignature() != null ? "Present" : "Not present",
                "insurerSignature", contract.getInsurerSignature() != null ? "Present" : "Not present",
                "isFullySigned", contract.getIsFullySigned() != null ? contract.getIsFullySigned() : false
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/test-signature-save")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSURER', 'CLIENT')")
    public ResponseEntity<?> testSignatureSave(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            System.out.println("=== TEST SIGNATURE SAVE ===");
            System.out.println("Contract ID: " + id);
            System.out.println("Signature length: " + (request.get("signature") != null ? request.get("signature").length() : "null"));
            
            Contract contract = contractService.getContractById(id);
            
            // Tester la sauvegarde d'une signature simple
            String testSignature = "test_signature_" + System.currentTimeMillis();
            contract.setClientSignature(testSignature);
            contract.setClientSignedAt(LocalDate.now());
            contract.setIsFullySigned(false);
            
            Contract saved = contractService.updateContract(id, contract);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Test signature saved successfully",
                "contractId", saved.getId(),
                "testSignature", testSignature
            ));
        } catch (Exception e) {
            System.out.println("Error in test signature save: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage(),
                "stackTrace", e.getStackTrace()[0].toString()
            ));
        }
    }

    @GetMapping("/{id}/debug-signatures")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSURER', 'CLIENT')")
    public ResponseEntity<?> debugSignatures(@PathVariable Long id) {
        try {
            Contract contract = contractService.getContractById(id);
            
            Map<String, Object> debugInfo = Map.of(
                "contractId", contract.getId(),
                "clientSignature", contract.getClientSignature() != null ? "PRÉSENTE (" + contract.getClientSignature().length() + " chars)" : "ABSENTE",
                "insurerSignature", contract.getInsurerSignature() != null ? "PRÉSENTE (" + contract.getInsurerSignature().length() + " chars)" : "ABSENTE",
                "clientSignedAt", contract.getClientSignedAt() != null ? contract.getClientSignedAt().toString() : "NON SIGNÉ",
                "insurerSignedAt", contract.getInsurerSignedAt() != null ? contract.getInsurerSignedAt().toString() : "NON SIGNÉ",
                "isFullySigned", contract.getIsFullySigned() != null ? contract.getIsFullySigned() : false,
                "status", contract.getStatus()
            );
            
            return ResponseEntity.ok(debugInfo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "stackTrace", e.getStackTrace()[0].toString()
            ));
        }
    }

    // 🔁 DTO → Entity avec calcul de date
    private Contract mapDtoToEntity(ContractRequestDto dto) {
        Contract contract = new Contract();
        contract.setClientId(dto.getClientId());
        contract.setInsurerId(dto.getInsurerId());
        contract.setBeneficiaryId(dto.getBeneficiaryId());

        // ✅ Création : aujourd’hui
        LocalDate creationDate = LocalDate.now();
        contract.setCreationDate(creationDate);

        // ✅ Calcul de endDate à partir de duration
        LocalDate endDate = calculateEndDateFromDuration(creationDate, dto.getDuration());
        contract.setEndDate(endDate);

        // ✅ Calcul automatique du statut
        String status = LocalDate.now().isBefore(endDate) ? "ACTIVE" : "INACTIVE";
        contract.setStatus(status);

        contract.setMontant(dto.getMontant());
        return contract;
    }


    private LocalDate calculateEndDateFromDuration(LocalDate creationDate, String duration) {
        return switch (duration.toLowerCase()) {
            case "6 mois" -> creationDate.plusMonths(6);
            case "1 an" -> creationDate.plusYears(1);
            case "2 ans" -> creationDate.plusYears(2);
            case "3 ans" -> creationDate.plusYears(3);
            case "4 ans" -> creationDate.plusYears(4);
            case "5 ans" -> creationDate.plusYears(5);
            default -> throw new IllegalArgumentException("Durée invalide : " + duration);
        };
    }

    private ContractResponseDto mapEntityToDto(Contract contract) {
        ContractResponseDto dto = new ContractResponseDto();
        dto.setId(contract.getId());
        dto.setClientId(contract.getClientId());
        dto.setInsurerId(contract.getInsurerId());
        dto.setBeneficiaryId(contract.getBeneficiaryId());
        dto.setCreationDate(contract.getCreationDate());
        dto.setEndDate(contract.getEndDate());
        dto.setStatus(contract.getStatus());
        dto.setMontant(contract.getMontant());
        return dto;
    }
}
