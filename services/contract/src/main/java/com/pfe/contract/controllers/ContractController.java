package com.pfe.contract.controllers;

import com.pfe.contract.dtos.ContractRequestDto;
import com.pfe.contract.dtos.ContractResponseDto;
import com.pfe.contract.entities.Contract;
import com.pfe.contract.services.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
