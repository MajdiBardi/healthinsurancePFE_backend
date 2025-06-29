package com.pfe.contract.repositories;

import com.pfe.contract.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    // Additional query methods if needed
    List <Contract> findByClientId(String clientId);

}