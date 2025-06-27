package com.pfe.contract.repositories;

import com.pfe.contract.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    // Additional query methods if needed
}