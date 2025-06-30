package com.pfe.contract.services;

import com.pfe.contract.dtos.ContractResponseDto;
import com.pfe.contract.entities.Contract;
import com.pfe.contract.repositories.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;

    @Override
    public Contract createContract(Contract contract) {
        contract.setCreationDate(LocalDate.now()); // ✅ Date de création automatique
        return contractRepository.save(contract);
    }

    @Override
    public Contract updateContract(Long id, Contract updatedContract) {
        Contract existing = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        existing.setClientId(updatedContract.getClientId());
        existing.setInsurerId(updatedContract.getInsurerId());
        existing.setBeneficiaryId(updatedContract.getBeneficiaryId());
        existing.setEndDate(updatedContract.getEndDate());
        existing.setMontant(updatedContract.getMontant());
        existing.setStatus(updatedContract.getStatus());

        return contractRepository.save(existing);
    }

    @Override
    public void deleteContract(Long id) {
        contractRepository.deleteById(id);
    }

    @Override
    public Contract getContractById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
    }

    @Override
    public List<Contract> getAllContracts() {
        List<Contract> contracts = contractRepository.findAll();
        for (Contract c : contracts) {
            c.setStatus(LocalDate.now().isBefore(c.getEndDate()) ? "ACTIVE" : "INACTIVE");
        }
        return contracts;
    }


    @Override
    public ContractResponseDto getContractDetails(Long id) {
        Contract contract = getContractById(id);
        ContractResponseDto dto = new ContractResponseDto();
        dto.setId(contract.getId());
        dto.setClientId(contract.getClientId());
        dto.setInsurerId(contract.getInsurerId());
        dto.setBeneficiaryId(contract.getBeneficiaryId());
        dto.setCreationDate(contract.getCreationDate());
        dto.setEndDate(contract.getEndDate());
        dto.setMontant(contract.getMontant());
        dto.setStatus(contract.getStatus());
        return dto;
    }

    @Override
    public List<Contract> getContractsByClientId(String clientId) {
        return contractRepository.findByClientId(clientId);
    }
}
