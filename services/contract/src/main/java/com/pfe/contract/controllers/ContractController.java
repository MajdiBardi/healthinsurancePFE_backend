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

    // ✅ CREATE - accessible par ADMIN ou INSURER
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSURER')")
    public ResponseEntity<ContractResponseDto> createContract(@RequestBody ContractRequestDto dto) {
        Contract contract = mapDtoToEntity(dto);
        Contract created = contractService.createContract(contract);
        return ResponseEntity.ok(mapEntityToDto(created));
    }

    // ✅ DÉTAILS enrichis - accessible par ADMIN ou INSURER
    @GetMapping("/{id}/details")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSURER')")
    public ResponseEntity<ContractResponseDto> getContractDetails(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getContractDetails(id));
    }

    // ✅ UPDATE - accessible par ADMIN uniquement
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Contract> updateContract(@PathVariable Long id, @RequestBody ContractRequestDto dto) {
        Contract contract = mapDtoToEntity(dto);
        return ResponseEntity.ok(contractService.updateContract(id, contract));
    }

    // ✅ GET by ID - accessible par ADMIN ou INSURER
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSURER')")
    public ResponseEntity<Contract> getContract(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getContractById(id));
    }

    // ✅ DELETE - accessible par ADMIN uniquement
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ LIST ALL - accessible par ADMIN ou INSURER
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSURER')")
    public ResponseEntity<List<Contract>> getAllContracts() {
        return ResponseEntity.ok(contractService.getAllContracts());
    }

    // ✅ CLIENT : voir uniquement ses propres contrats
    @GetMapping("/my-contracts")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<Contract>> getMyContracts(Authentication authentication) {
        String userId = authentication.getName(); // keycloak ID
        return ResponseEntity.ok(contractService.getContractsByClientId(userId));
    }

    // 🔁 Méthode utilitaire : DTO → Entity
    private Contract mapDtoToEntity(ContractRequestDto dto) {
        Contract contract = new Contract();
        contract.setClientId(dto.getClientId());
        contract.setInsurerId(dto.getInsurerId());
        contract.setBeneficiaryId(dto.getBeneficiaryId());
        contract.setCreationDate(LocalDate.parse(dto.getCreationDate()));
        contract.setEndDate(LocalDate.parse(dto.getEndDate()));
        contract.setStatus(dto.getStatus());
        contract.setMontant(dto.getMontant());
        return contract;
    }

    // 🔁 Méthode utilitaire : Entity → DTO
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
