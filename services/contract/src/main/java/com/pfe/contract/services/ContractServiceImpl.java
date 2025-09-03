package com.pfe.contract.services;

import com.pfe.contract.clients.NotificationClient;
import com.pfe.contract.clients.PaymentClient;
import com.pfe.contract.clients.UserClient;
import com.pfe.contract.dtos.ContractResponseDto;
import com.pfe.contract.dtos.NotificationEmailRequest;
import com.pfe.contract.dtos.UserDto;
import com.pfe.contract.entities.Contract;
import com.pfe.contract.repositories.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final NotificationClient notificationClient;
    private final UserClient userClient;
    private final PaymentClient paymentClient; // ✅ Ajouté

    @Override
    public Contract createContract(Contract contract) {
        contract.setCreationDate(LocalDate.now());
        Contract saved = contractRepository.save(contract);

        // ✅ Envoi email
        sendNotificationEmail(saved);

        // ✅ Création paiement Paymee
        createInitialTransaction(saved);

        return saved;
    }

    @Override
    public Contract updateContract(Long id, Contract updatedContract) {
        Contract existing = contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        existing.setClientId(updatedContract.getClientId());
        existing.setInsurerId(updatedContract.getInsurerId());
        existing.setBeneficiaryId(updatedContract.getBeneficiaryId());
        existing.setEndDate(updatedContract.getEndDate());
        existing.setMontant(updatedContract.getMontant());
        existing.setStatus(updatedContract.getStatus());

        return contractRepository.save(existing);
    }

    @Override
    public void deleteContract(Long id) {
        contractRepository.deleteById(id);
    }

    @Override
    public Contract getContractById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
    }

    @Override
    public List<Contract> getAllContracts() {
        List<Contract> contracts = contractRepository.findAll();
        for (Contract c : contracts) {
            updateStatusIfExpired(c);
        }
        return contracts;
    }

    @Override
    public ContractResponseDto getContractDetails(Long id) {
        Contract contract = getContractById(id);
        updateStatusIfExpired(contract);

        ContractResponseDto dto = new ContractResponseDto();
        dto.setId(contract.getId());
        dto.setClientId(contract.getClientId());
        dto.setInsurerId(contract.getInsurerId());
        dto.setBeneficiaryId(contract.getBeneficiaryId());
        dto.setCreationDate(contract.getCreationDate());
        dto.setEndDate(contract.getEndDate());
        dto.setMontant(contract.getMontant());
        dto.setStatus(contract.getStatus());
        return dto;
    }

    @Override
    public List<Contract> getContractsByClientId(String clientId) {
        List<Contract> contracts = contractRepository.findByClientId(clientId);
        for (Contract c : contracts) {
            updateStatusIfExpired(c);
        }
        return contracts;
    }

    private void updateStatusIfExpired(Contract contract) {
        LocalDate now = LocalDate.now();
        if (contract.getEndDate() != null) {
            contract.setStatus(now.isBefore(contract.getEndDate()) ? "ACTIVE" : "INACTIVE");
        }
    }


    private void sendNotificationEmail(Contract contract) {
        UserDto user = userClient.getUserById(contract.getClientId());
        UserDto insurer = userClient.getUserById(contract.getInsurerId());
        UserDto beneficiary = userClient.getUserById(contract.getBeneficiaryId());

        String subject = "Nouveau contrat créé";
        String body = "<div style='font-family:Segoe UI,Arial,sans-serif;font-size:15px;color:#222;'>"
                + "<h2 style='color:#1976d2;'>Bonjour " + user.getName() + ",</h2>"
                + "<p>Votre nouveau contrat d'assurance a été créé avec succès.</p>"
                + "<table style='background:#f4f8fb;padding:16px 24px;border-radius:10px;margin:18px 0;'>"
                + "<tr><td><b>Montant :</b></td><td>" + contract.getMontant() + " DT</td></tr>"
                + "<tr><td><b>Date de début :</b></td><td>" + contract.getCreationDate() + "</td></tr>"
                + "<tr><td><b>Date de fin :</b></td><td>" + contract.getEndDate() + "</td></tr>"
                + "<tr><td><b>Assureur :</b></td><td>" + (insurer != null ? insurer.getName() : "") + "</td></tr>"
                + "<tr><td><b>Bénéficiaire :</b></td><td>" + (beneficiary != null ? beneficiary.getName() : "") + "</td></tr>"
                + "</table>"
                + "<p style='color:#1976d2;'>Merci pour votre confiance.<br>Vermeg Life Insurance</p>"
                + "</div>";

        NotificationEmailRequest request = new NotificationEmailRequest();
        request.setTo(user.getEmail());
        request.setSubject(subject);
        request.setBody(body);
        //request.setHtml(true); // Si ton système le supporte

        notificationClient.sendEmail(request);
    }

    // ✅ Création du lien de paiement via Paymee
    private void createInitialTransaction(Contract contract) {
        try {
            String url = paymentClient.initiatePaymeePayment(
                    contract.getId().toString(),
                    BigDecimal.valueOf(contract.getMontant())
            );

            System.out.println("✅ Lien de paiement Paymee généré : " + url);

            // (Optionnel) tu peux envoyer ce lien par email, le stocker, etc.

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l’appel à Paymee : " + e.getMessage());
        }
    }
}
