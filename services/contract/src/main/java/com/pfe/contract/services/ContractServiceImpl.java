package com.pfe.contract.services;

import com.pfe.contract.clients.UserClient;
import com.pfe.contract.dtos.ContractResponseDto;
import com.pfe.contract.dtos.UserDto;
import com.pfe.contract.entities.Contract;
import com.pfe.contract.repositories.ContractRepository;
import com.pfe.contract.services.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final UserClient userClient;

    @Override
    public Contract createContract(Contract contract) {
        return contractRepository.save(contract);
    }

    @Override
    public Contract updateContract(String id, Contract contract) {
        Contract existing = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
        existing.setCreationDate(contract.getCreationDate());
        existing.setEndDate(contract.getEndDate());
        existing.setStatus(contract.getStatus());
        existing.setClientId(contract.getClientId());
        existing.setInsurerId(contract.getInsurerId());
        return contractRepository.save(existing);
    }

    @Override
    public void deleteContract(String id) {
        contractRepository.deleteById(id);
    }

    @Override
    public Contract getContractById(String id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
    }

    @Override
    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }
    public ContractResponseDto getContractDetails(String id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        UserDto client = userClient.getUserById(contract.getClientId());
        UserDto insurer = userClient.getUserById(contract.getInsurerId());
        UserDto beneficiary = userClient.getUserById(contract.getBeneficiaryId());

        ContractResponseDto dto = new ContractResponseDto();
        dto.setId(contract.getId());
        dto.setCreationDate(contract.getCreationDate());
        dto.setEndDate(contract.getEndDate());
        dto.setStatus(contract.getStatus());


        dto.setClientId(client.getId());
        dto.setClientName(client.getName());

        dto.setInsurerId(insurer.getId());
        dto.setInsurerName(insurer.getName());

        dto.setBeneficiaryId(beneficiary.getId());
        dto.setBeneficiaryName(beneficiary.getName());

        return dto;
    }
}