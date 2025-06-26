package com.pfe.contract.controllers;

import com.pfe.contract.dtos.ContractRequestDto;
import com.pfe.contract.dtos.ContractResponseDto;
import com.pfe.contract.entities.Contract;
import com.pfe.contract.services.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    // ✅ CREATE
    @PostMapping
    public ResponseEntity<Contract> createContract(@RequestBody ContractRequestDto dto) {
        Contract contract = mapDtoToEntity(dto);
        return ResponseEntity.ok(contractService.createContract(contract));
    }
    @GetMapping("/{id}/details")
    public ResponseEntity<ContractResponseDto> getContractDetails(@PathVariable String id) {
        return ResponseEntity.ok(contractService.getContractDetails(id));
    }


    // ✅ UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Contract> updateContract(@PathVariable String id, @RequestBody ContractRequestDto dto) {
        Contract contract = mapDtoToEntity(dto);
        return ResponseEntity.ok(contractService.updateContract(id, contract));
    }

    // ✅ READ
    @GetMapping("/{id}")
    public ResponseEntity<Contract> getContract(@PathVariable String id) {
        return ResponseEntity.ok(contractService.getContractById(id));
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable String id) {
        contractService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ LIST
    @GetMapping
    public ResponseEntity<List<Contract>> getAllContracts() {
        return ResponseEntity.ok(contractService.getAllContracts());
    }

    // 🧱 Méthode utilitaire pour conversion DTO → Entity
    private Contract mapDtoToEntity(ContractRequestDto dto) {
        Contract contract = new Contract();
        contract.setClientId(dto.getClientId());
        contract.setInsurerId(dto.getInsurerId());
        contract.setBeneficiaryId(dto.getBeneficiaryId());
        contract.setCreationDate(dto.getCreationDate());
        contract.setEndDate(dto.getEndDate());
        contract.setStatus(dto.getStatus());
        return contract;
    }
}
