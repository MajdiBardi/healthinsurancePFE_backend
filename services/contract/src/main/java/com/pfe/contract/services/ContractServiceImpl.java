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
        
        // Mettre à jour les champs de signature
        if (updatedContract.getClientSignature() != null) {
            existing.setClientSignature(updatedContract.getClientSignature());
        }
        if (updatedContract.getInsurerSignature() != null) {
            existing.setInsurerSignature(updatedContract.getInsurerSignature());
        }
        if (updatedContract.getClientSignedAt() != null) {
            existing.setClientSignedAt(updatedContract.getClientSignedAt());
        }
        if (updatedContract.getInsurerSignedAt() != null) {
            existing.setInsurerSignedAt(updatedContract.getInsurerSignedAt());
        }
        if (updatedContract.getIsFullySigned() != null) {
            existing.setIsFullySigned(updatedContract.getIsFullySigned());
        }

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
        
        // Ajouter les informations de signature
        dto.setClientSignature(contract.getClientSignature());
        dto.setInsurerSignature(contract.getInsurerSignature());
        dto.setClientSignedAt(contract.getClientSignedAt());
        dto.setInsurerSignedAt(contract.getInsurerSignedAt());
        dto.setIsFullySigned(contract.getIsFullySigned());
        
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

        String subject = "🎉 Votre contrat d'assurance Vermeg Life Insurance a été créé avec succès";
        
        String body = "<!DOCTYPE html>" +
                "<html lang='fr'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Contrat d'assurance créé</title>" +
                "</head>" +
                "<body style='margin:0;padding:0;background-color:#f8fafc;font-family:\"Segoe UI\",Arial,sans-serif;'>" +
                
                // Container principal
                "<div style='max-width:600px;margin:0 auto;background-color:#ffffff;box-shadow:0 4px 20px rgba(0,0,0,0.1);'>" +
                
                // Header avec logo et gradient
                "<div style='background:linear-gradient(135deg,#1976d2 0%,#42a5f5 100%);padding:40px 30px;text-align:center;'>" +
                "<div style='background-color:#ffffff;width:60px;height:60px;border-radius:50%;margin:0 auto 20px;display:flex;align-items:center;justify-content:center;'>" +
                "<div style='width:40px;height:40px;background:linear-gradient(135deg,#1976d2,#42a5f5);border-radius:50%;'></div>" +
                "</div>" +
                "<h1 style='color:#ffffff;margin:0;font-size:28px;font-weight:700;letter-spacing:-0.5px;'>Vermeg Life Insurance</h1>" +
                "<p style='color:#e3f2fd;margin:8px 0 0;font-size:16px;font-weight:300;'>Votre partenaire de confiance</p>" +
                "</div>" +
                
                // Contenu principal
                "<div style='padding:40px 30px;'>" +
                
                // Message de bienvenue
                "<div style='text-align:center;margin-bottom:30px;'>" +
                "<h2 style='color:#1e293b;margin:0 0 10px;font-size:24px;font-weight:600;'>Félicitations " + user.getName() + " !</h2>" +
                "<p style='color:#64748b;margin:0;font-size:16px;line-height:1.6;'>Votre contrat d'assurance a été créé avec succès et est maintenant actif.</p>" +
                "</div>" +
                
                // Carte de contrat avec design moderne
                "<div style='background:linear-gradient(135deg,#f8fafc 0%,#e2e8f0 100%);border-radius:16px;padding:30px;margin:30px 0;border:1px solid #e2e8f0;'>" +
                "<div style='display:flex;align-items:center;margin-bottom:20px;'>" +
                "<div style='width:48px;height:48px;background:linear-gradient(135deg,#1976d2,#42a5f5);border-radius:12px;display:flex;align-items:center;justify-content:center;margin-right:16px;'>" +
                "<svg width='24' height='24' viewBox='0 0 24 24' fill='white'>" +
                "<path d='M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z'/>" +
                "<polyline points='14,2 14,8 20,8'/>" +
                "<line x1='16' y1='13' x2='8' y2='13'/>" +
                "<line x1='16' y1='17' x2='8' y2='17'/>" +
                "</svg>" +
                "</div>" +
                "<div>" +
                "<h3 style='color:#1e293b;margin:0;font-size:20px;font-weight:700;'>Contrat #" + contract.getId() + "</h3>" +
                "<p style='color:#64748b;margin:4px 0 0;font-size:14px;'>Contrat d'assurance vie</p>" +
                "</div>" +
                "</div>" +
                
                // Détails du contrat
                "<div style='display:grid;grid-template-columns:1fr 1fr;gap:20px;margin-bottom:20px;'>" +
                "<div style='background:#ffffff;padding:20px;border-radius:12px;border:1px solid #e2e8f0;'>" +
                "<div style='color:#64748b;font-size:12px;font-weight:600;text-transform:uppercase;letter-spacing:0.5px;margin-bottom:8px;'>Montant</div>" +
                "<div style='color:#1e293b;font-size:24px;font-weight:700;'>" + contract.getMontant() + " DT</div>" +
                "</div>" +
                "<div style='background:#ffffff;padding:20px;border-radius:12px;border:1px solid #e2e8f0;'>" +
                "<div style='color:#64748b;font-size:12px;font-weight:600;text-transform:uppercase;letter-spacing:0.5px;margin-bottom:8px;'>Statut</div>" +
                "<div style='color:#16a34a;font-size:16px;font-weight:600;'>✓ Actif</div>" +
                "</div>" +
                "</div>" +
                
                // Informations détaillées
                "<div style='background:#ffffff;border-radius:12px;border:1px solid #e2e8f0;overflow:hidden;'>" +
                "<div style='background:#f8fafc;padding:16px 20px;border-bottom:1px solid #e2e8f0;'>" +
                "<h4 style='color:#1e293b;margin:0;font-size:16px;font-weight:600;'>Détails du contrat</h4>" +
                "</div>" +
                "<div style='padding:20px;'>" +
                "<div style='display:flex;justify-content:space-between;padding:12px 0;border-bottom:1px solid #f1f5f9;'>" +
                "<span style='color:#64748b;font-weight:500;'>Date de début</span>" +
                "<span style='color:#1e293b;font-weight:600;'>" + contract.getCreationDate() + "</span>" +
                "</div>" +
                "<div style='display:flex;justify-content:space-between;padding:12px 0;border-bottom:1px solid #f1f5f9;'>" +
                "<span style='color:#64748b;font-weight:500;'>Date de fin</span>" +
                "<span style='color:#1e293b;font-weight:600;'>" + contract.getEndDate() + "</span>" +
                "</div>" +
                "<div style='display:flex;justify-content:space-between;padding:12px 0;border-bottom:1px solid #f1f5f9;'>" +
                "<span style='color:#64748b;font-weight:500;'>Assureur</span>" +
                "<span style='color:#1e293b;font-weight:600;'>" + (insurer != null ? insurer.getName() : "Non défini") + "</span>" +
                "</div>" +
                "<div style='display:flex;justify-content:space-between;padding:12px 0;'>" +
                "<span style='color:#64748b;font-weight:500;'>Bénéficiaire</span>" +
                "<span style='color:#1e293b;font-weight:600;'>" + (beneficiary != null ? beneficiary.getName() : "Non défini") + "</span>" +
                "</div>" +
                "</div>" +
                "</div>" +
                
                "</div>" +
                
                // Section d'actions
                "<div style='background:#f8fafc;padding:30px;text-align:center;border-top:1px solid #e2e8f0;'>" +
                "<h3 style='color:#1e293b;margin:0 0 16px;font-size:18px;font-weight:600;'>Prochaines étapes</h3>" +
                "<p style='color:#64748b;margin:0 0 24px;font-size:14px;line-height:1.6;'>Votre contrat est maintenant actif. Vous pouvez accéder à votre espace client pour suivre l'état de votre contrat et effectuer vos paiements.</p>" +
                "<a href='#' style='display:inline-block;background:linear-gradient(135deg,#1976d2,#42a5f5);color:#ffffff;text-decoration:none;padding:14px 28px;border-radius:8px;font-weight:600;font-size:16px;box-shadow:0 4px 12px rgba(25,118,210,0.3);transition:all 0.2s ease;'>Accéder à mon espace</a>" +
                "</div>" +
                
                // Footer
                "<div style='background:#1e293b;padding:30px;text-align:center;'>" +
                "<div style='color:#94a3b8;font-size:14px;line-height:1.6;'>" +
                "<p style='margin:0 0 16px;'>Merci pour votre confiance en Vermeg Life Insurance</p>" +
                "<div style='border-top:1px solid #334155;padding-top:16px;margin-top:16px;'>" +
                "<p style='margin:0;font-size:12px;'>© 2025 Vermeg Life Insurance. Tous droits réservés.</p>" +
                "<p style='margin:4px 0 0;font-size:12px;'>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>" +
                "</div>" +
                "</div>" +
                "</div>" +
                
                "</div>" +
                "</body>" +
                "</html>";

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
