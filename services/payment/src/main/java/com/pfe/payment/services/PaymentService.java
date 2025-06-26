package com.pfe.payment.services;

import java.math.BigDecimal;

public interface PaymentService {
    String initiatePaymeePayment(String contractId, BigDecimal amount);

    void handlePaymeeCallback(String orderId, String paymeeTxId); // ✅ Ajoute cette ligne
}