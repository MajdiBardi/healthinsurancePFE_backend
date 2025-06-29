package com.pfe.contract.services;

import com.pfe.contract.dtos.ContractResponseDto;
import com.pfe.contract.entities.Contract;

import java.util.List;

public interface ContractService {
    Contract createContract(Contract contract);
    Contract updateContract(Long id, Contract contract);
    void deleteContract(Long id);
    Contract getContractById(Long id);
    List<Contract> getAllContracts();
    ContractResponseDto getContractDetails(Long id);
    List<Contract> getContractsByClientId(String clientId);

}
