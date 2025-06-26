package com.pfe.payment.controllers;

import com.pfe.payment.dtos.ManualPaymentRequest;
import com.pfe.payment.entities.Transaction;
import com.pfe.payment.services.PaymentService;
import com.pfe.payment.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final TransactionService transactionService;

    @PostMapping("/paymee/{contractId}")
    public ResponseEntity<String> initiatePaymee(@PathVariable String contractId,
                                                 @RequestParam BigDecimal amount) {
        String checkoutUrl = paymentService.initiatePaymeePayment(contractId, amount);
        return ResponseEntity.ok(checkoutUrl);
    }

    @PostMapping("/manual")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Transaction> manualPayment(@RequestBody ManualPaymentRequest request,
                                                     @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                transactionService.createManualTransaction(request, jwt.getSubject())
        );
    }

    @PostMapping("/callback")
    public ResponseEntity<Void> paymeeCallback(@RequestBody Map<String, Object> payload) {
        String orderId = (String) payload.get("order_id");
        String txId = (String) payload.get("transaction");
        paymentService.handlePaymeeCallback(orderId, txId);
        return ResponseEntity.ok().build();
    }
}