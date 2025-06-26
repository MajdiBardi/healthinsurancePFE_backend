package com.pfe.payment.repositories;

import com.pfe.payment.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByContractId(String contractId);
}