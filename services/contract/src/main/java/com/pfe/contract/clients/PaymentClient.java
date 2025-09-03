package com.pfe.contract.clients;

import com.pfe.contract.dtos.TransactionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "payment" ,path = "/api/payments")
public interface PaymentClient {

    @PostMapping("/paymee/{contractId}")
    String initiatePaymeePayment(@PathVariable String contractId,
                                 @RequestParam("amount") BigDecimal amount);
}