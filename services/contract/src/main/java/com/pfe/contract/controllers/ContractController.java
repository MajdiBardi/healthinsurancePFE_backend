package com.pfe.contract.controllers;

import com.pfe.contract.dtos.ContractRequestDto;
import com.pfe.contract.dtos.ContractResponseDto;
import com.pfe.contract.entities.Contract;
import com.pfe.contract.services.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    // ✅ CREATE
    @PostMapping
    public ResponseEntity<ContractResponseDto> createContract(@RequestBody ContractRequestDto dto) {
        Contract contract = mapDtoToEntity(dto);
        Contract created = contractService.createContract(contract);
        return ResponseEntity.ok(mapEntityToDto(created));
    }

    // ✅ Détails enrichis
    @GetMapping("/{id}/details")
    public ResponseEntity<ContractResponseDto> getContractDetails(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getContractDetails(id));
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Contract> updateContract(@PathVariable Long id, @RequestBody ContractRequestDto dto) {
        Contract contract = mapDtoToEntity(dto);
        return ResponseEntity.ok(contractService.updateContract(id, contract));
    }

    // ✅ READ
    @GetMapping("/{id}")
    public ResponseEntity<Contract> getContract(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getContractById(id));
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ LIST
    @GetMapping
    public ResponseEntity<List<Contract>> getAllContracts() {
        return ResponseEntity.ok(contractService.getAllContracts());
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
        contract.setMontant(dto.getMontant()); // ✅ Ajout montant
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
        dto.setMontant(contract.getMontant()); // ✅ Ajout montant
        return dto;
    }
}
