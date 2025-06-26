package com.pfe.payment.services;

import com.pfe.payment.dtos.ManualPaymentRequest;
import com.pfe.payment.entities.Contract;
import com.pfe.payment.entities.Payment;
import com.pfe.payment.entities.Transaction;
import com.pfe.payment.entities.TransactionType;
import com.pfe.payment.repositories.ContractRepository;
import com.pfe.payment.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;


    @Service
    @RequiredArgsConstructor
    public class TransactionServiceImpl implements TransactionService {

        private final TransactionRepository transactionRepository;
        private final ContractRepository contractRepository;

        @Override
        public Transaction createFromPayment(Payment payment, String validatedBy) {
            return transactionRepository.save(Transaction.builder()
                    .id(UUID.randomUUID().toString())
                    .type(TransactionType.PAYMENT_ONLINE)
                    .amount(payment.getAmount())
                    .date(LocalDate.now())
                    .status("validated")
                    .validatedBy(validatedBy)
                    .contract(payment.getContract())
                    .build());
        }

        @Override
        public Transaction createManualTransaction(ManualPaymentRequest request, String adminId) {
            Contract contract = contractRepository.findById(request.getContractId())
                    .orElseThrow(() -> new RuntimeException("Contrat non trouvé"));

            return transactionRepository.save(Transaction.builder()
                    .id(UUID.randomUUID().toString())
                    .type(request.getType())
                    .amount(request.getAmount())
                    .date(LocalDate.now())
                    .status("validated")
                    .validatedBy(adminId)
                    .contract(contract)
                    .build());
        }
    }

