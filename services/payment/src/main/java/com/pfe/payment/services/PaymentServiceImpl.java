package com.pfe.payment.services;

import com.pfe.payment.Clients.ContractClient;
import com.pfe.payment.dtos.ContractDto;
import com.pfe.payment.entities.Contract;
import com.pfe.payment.entities.Payment;
import com.pfe.payment.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionService transactionService;
    private final ContractClient contractClient; // ✅ appel Feign
    private final RestTemplate restTemplate;

    private static final String PAYMEE_URL = "https://sandbox.paymee.tn/api/v1/payments/create";
    private static final String AUTH_HEADER = "Bearer 88dad2f8a7db654d6326a0814ad090b3e30cad0a"; // remplace par un vrai token

    @Override
    public String initiatePaymeePayment(String contractId, BigDecimal amount) {
        // 🔍 Vérifier que le contrat existe dans le microservice contract
        ContractDto contractDto;
        try {
            contractDto = contractClient.getContract(contractId);
        } catch (Exception e) {
            throw new RuntimeException("Contrat introuvable dans contract-service");
        }

        // Créer un objet Contract local pour la persistance
        Contract contract = Contract.builder().id(contractId).build();

        // Enregistrer le paiement
        Payment payment = Payment.builder()
                .id(UUID.randomUUID().toString())
                .amount(amount)
                .status("PENDING")
                .contract(contract)
                .build();

        paymentRepository.save(payment);

        // Préparer appel à Paymee
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", AUTH_HEADER);

        Map<String, Object> body = Map.of(
                "vendor", "3819",
                "amount", amount,
                "note", "Paiement contrat " + contractId,
                "order_id", payment.getId(),
                "back_url", "http://localhost:8083/api/payments/callback"
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(PAYMEE_URL, request, Map.class);

        String checkoutUrl = (String) ((Map) response.getBody().get("data")).get("checkout_url");

        return checkoutUrl;
    }

    @Override
    public void handlePaymeeCallback(String orderId, String paymeeTxId) {
        Payment payment = paymentRepository.findById(orderId).orElseThrow();
        if (!payment.getStatus().equals("PAID")) {
            payment.setStatus("PAID");
            payment.setPaymeeTransactionId(paymeeTxId);
            paymentRepository.save(payment);

            transactionService.createFromPayment(payment, "Paymee");
        }
    }
}
