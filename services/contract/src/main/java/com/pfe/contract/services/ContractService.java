package com.pfe.contract.services;

import com.pfe.contract.dtos.ContractResponseDto;
import com.pfe.contract.entities.Contract;

import java.util.List;

public interface ContractService {
    Contract createContract(Contract contract);
    Contract updateContract(String id, Contract contract);
    void deleteContract(String id);
    Contract getContractById(String id);
    List<Contract> getAllContracts();
    ContractResponseDto getContractDetails(String id);

}