package com.pfe.payment.services;

import com.pfe.payment.dtos.ManualPaymentRequest;
import com.pfe.payment.entities.Payment;
import com.pfe.payment.entities.Transaction;

public interface TransactionService {
    Transaction createFromPayment(Payment payment, String validatedBy);
    Transaction createManualTransaction(ManualPaymentRequest request, String adminId);
}