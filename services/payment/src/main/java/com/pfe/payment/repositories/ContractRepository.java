package com.pfe.payment.repositories;

import com.pfe.payment.entities.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, String> {
}