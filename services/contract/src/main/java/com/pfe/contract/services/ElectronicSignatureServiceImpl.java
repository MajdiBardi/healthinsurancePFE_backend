package com.pfe.contract.services;

import com.pfe.contract.clients.NotificationClient;
import com.pfe.contract.clients.UserClient;
import com.pfe.contract.dtos.NotificationEmailRequest;
import com.pfe.contract.dtos.SignatureRequestDto;
import com.pfe.contract.dtos.SignatureResponseDto;
import com.pfe.contract.dtos.UserDto;
import com.pfe.contract.entities.Contract;
import com.pfe.contract.repositories.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ElectronicSignatureServiceImpl implements ElectronicSignatureService {

    private final ContractRepository contractRepository;
    private final NotificationClient notificationClient;
    private final UserClient userClient;

    @Override
    public SignatureResponseDto signContract(SignatureRequestDto request) {
        System.out.println("=== SIGNATURE REQUEST ===");
        System.out.println("Contract ID: " + request.getContractId());
        System.out.println("Signer ID: " + request.getSignerId());
        System.out.println("Signer Role: " + request.getSignerRole());
        System.out.println("Signature length: " + (request.getSignature() != null ? request.getSignature().length() : "null"));
        System.out.println("Signature preview: " + (request.getSignature() != null ? request.getSignature().substring(0, Math.min(50, request.getSignature().length())) + "..." : "null"));
        
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        // Vérifier que la signature n'est pas vide
        if (request.getSignature() == null || request.getSignature().trim().isEmpty()) {
            throw new RuntimeException("Signature cannot be empty");
        }

        // Vérifier que la signature est valide (base64) - plus permissif
        if (!isValidBase64Signature(request.getSignature())) {
            System.out.println("Warning: Signature may not be valid base64, but proceeding anyway");
        }

        // Appliquer la signature selon le rôle
        System.out.println("🔍 Rôle reçu: " + request.getSignerRole());
        System.out.println("🔍 ID du signataire: " + request.getSignerId());
        
        if ("CLIENT".equals(request.getSignerRole())) {
            contract.setClientSignature(request.getSignature());
            contract.setClientSignedAt(LocalDate.now());
            System.out.println("✅ Signature CLIENT appliquée pour l'utilisateur: " + request.getSignerId());
        } else if ("INSURER".equals(request.getSignerRole())) {
            contract.setInsurerSignature(request.getSignature());
            contract.setInsurerSignedAt(LocalDate.now());
            System.out.println("✅ Signature INSURER appliquée pour l'utilisateur: " + request.getSignerId());
        } else {
            System.out.println("❌ Rôle invalide: " + request.getSignerRole());
            throw new RuntimeException("Invalid signer role: " + request.getSignerRole());
        }

        // Vérifier si le contrat est entièrement signé
        boolean isFullySigned = contract.getClientSignature() != null && 
                               contract.getInsurerSignature() != null;
        contract.setIsFullySigned(isFullySigned);
        
        System.out.println("📊 Statut des signatures:");
        System.out.println("  - Client signature: " + (contract.getClientSignature() != null ? "PRÉSENTE" : "ABSENTE"));
        System.out.println("  - Insurer signature: " + (contract.getInsurerSignature() != null ? "PRÉSENTE" : "ABSENTE"));
        System.out.println("  - Contrat entièrement signé: " + isFullySigned);

        Contract savedContract = contractRepository.save(contract);

        // Envoyer notification
        sendSignatureNotification(savedContract, request.getSignerRole());

        return mapToResponseDto(savedContract);
    }

    @Override
    public SignatureResponseDto getContractSignatureStatus(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        return mapToResponseDto(contract);
    }

    @Override
    public boolean verifySignature(Long contractId, String signerRole) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        if ("CLIENT".equals(signerRole)) {
            return contract.getClientSignature() != null && contract.getClientSignedAt() != null;
        } else if ("INSURER".equals(signerRole)) {
            return contract.getInsurerSignature() != null && contract.getInsurerSignedAt() != null;
        }

        return false;
    }

    private boolean isValidBase64Signature(String signature) {
        try {
            Base64.getDecoder().decode(signature);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void sendSignatureNotification(Contract contract, String signerRole) {
        try {
            NotificationEmailRequest email = new NotificationEmailRequest();
            
            // Récupérer les informations des utilisateurs
            UserDto client = userClient.getUserById(contract.getClientId());
            UserDto insurer = userClient.getUserById(contract.getInsurerId());
            
            if ("CLIENT".equals(signerRole)) {
                // Le client a signé, notifier l'assureur
                if (insurer != null && insurer.getEmail() != null) {
                    email.setTo(insurer.getEmail());
                    email.setSubject("✅ Signature client reçue - Contrat #" + contract.getId());
                    email.setBody(createSignatureNotificationTemplate(contract, "CLIENT", insurer.getName()));
                    System.out.println("📧 Notification envoyée à l'assureur: " + insurer.getEmail());
                } else {
                    System.out.println("❌ Impossible d'envoyer la notification: assureur non trouvé");
                    return;
                }
            } else {
                // L'assureur a signé, notifier le client
                if (client != null && client.getEmail() != null) {
                    email.setTo(client.getEmail());
                    email.setSubject("✅ Signature assureur reçue - Contrat #" + contract.getId());
                    email.setBody(createSignatureNotificationTemplate(contract, "INSURER", client.getName()));
                    System.out.println("📧 Notification envoyée au client: " + client.getEmail());
                } else {
                    System.out.println("❌ Impossible d'envoyer la notification: client non trouvé");
                    return;
                }
            }
            
            notificationClient.sendEmail(email);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi de notification: " + e.getMessage());
        }
    }
    
    private String createSignatureNotificationTemplate(Contract contract, String signerRole, String recipientName) {
        String signerName = "CLIENT".equals(signerRole) ? "Le client" : "L'assureur";
        String statusMessage = contract.getIsFullySigned() ? 
            "Le contrat est maintenant entièrement signé et actif." : 
            "Le contrat est maintenant prêt pour votre signature.";
        String statusColor = contract.getIsFullySigned() ? "#16a34a" : "#f59e0b";
        String statusIcon = contract.getIsFullySigned() ? "✓" : "⏳";
        
        return "<!DOCTYPE html>" +
                "<html lang='fr'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Notification de signature</title>" +
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
                "<p style='color:#e3f2fd;margin:8px 0 0;font-size:16px;font-weight:300;'>Signature électronique</p>" +
                "</div>" +
                
                // Contenu principal
                "<div style='padding:40px 30px;'>" +
                
                // Message principal
                "<div style='text-align:center;margin-bottom:30px;'>" +
                "<div style='width:80px;height:80px;background:linear-gradient(135deg," + statusColor + ",#ffffff);border-radius:50%;margin:0 auto 20px;display:flex;align-items:center;justify-content:center;border:4px solid #ffffff;box-shadow:0 4px 20px rgba(0,0,0,0.1);'>" +
                "<div style='width:60px;height:60px;background:" + statusColor + ";border-radius:50%;display:flex;align-items:center;justify-content:center;'>" +
                "<span style='color:#ffffff;font-size:32px;font-weight:700;'>" + statusIcon + "</span>" +
                "</div>" +
                "</div>" +
                "<h2 style='color:#1e293b;margin:0 0 10px;font-size:24px;font-weight:600;'>Signature reçue !</h2>" +
                "<p style='color:#64748b;margin:0;font-size:16px;line-height:1.6;'>Bonjour " + recipientName + ",<br>" + signerName + " a signé le contrat #" + contract.getId() + "</p>" +
                "</div>" +
                
                // Carte de statut
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
                "<p style='color:#64748b;margin:4px 0 0;font-size:14px;'>Montant: " + contract.getMontant() + " DT</p>" +
                "</div>" +
                "</div>" +
                
                // Statut des signatures
                "<div style='background:#ffffff;border-radius:12px;border:1px solid #e2e8f0;overflow:hidden;'>" +
                "<div style='background:#f8fafc;padding:16px 20px;border-bottom:1px solid #e2e8f0;'>" +
                "<h4 style='color:#1e293b;margin:0;font-size:16px;font-weight:600;'>Statut des signatures</h4>" +
                "</div>" +
                "<div style='padding:20px;'>" +
                "<div style='display:flex;justify-content:space-between;align-items:center;padding:12px 0;border-bottom:1px solid #f1f5f9;'>" +
                "<span style='color:#64748b;font-weight:500;'>Signature client</span>" +
                "<div style='display:flex;align-items:center;'>" +
                "<span style='color:" + (contract.getClientSignature() != null ? "#16a34a" : "#ef4444") + ";font-weight:600;margin-right:8px;'>" +
                (contract.getClientSignature() != null ? "✓ Signée" : "⏳ En attente") + "</span>" +
                "</div>" +
                "</div>" +
                "<div style='display:flex;justify-content:space-between;align-items:center;padding:12px 0;'>" +
                "<span style='color:#64748b;font-weight:500;'>Signature assureur</span>" +
                "<div style='display:flex;align-items:center;'>" +
                "<span style='color:" + (contract.getInsurerSignature() != null ? "#16a34a" : "#ef4444") + ";font-weight:600;margin-right:8px;'>" +
                (contract.getInsurerSignature() != null ? "✓ Signée" : "⏳ En attente") + "</span>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>" +
                
                // Message de statut
                "<div style='background:" + statusColor + ";color:#ffffff;padding:20px;border-radius:12px;margin:20px 0;text-align:center;'>" +
                "<h4 style='margin:0 0 8px;font-size:18px;font-weight:600;'>" + statusMessage + "</h4>" +
                "</div>" +
                
                "</div>" +
                
                // Section d'actions
                "<div style='background:#f8fafc;padding:30px;text-align:center;border-top:1px solid #e2e8f0;'>" +
                "<h3 style='color:#1e293b;margin:0 0 16px;font-size:18px;font-weight:600;'>Actions disponibles</h3>" +
                "<p style='color:#64748b;margin:0 0 24px;font-size:14px;line-height:1.6;'>" +
                (contract.getIsFullySigned() ? 
                    "Votre contrat est maintenant entièrement signé et actif. Vous pouvez télécharger le PDF final." :
                    "Le contrat nécessite encore votre signature pour être complet.") +
                "</p>" +
                "<a href='#' style='display:inline-block;background:linear-gradient(135deg,#1976d2,#42a5f5);color:#ffffff;text-decoration:none;padding:14px 28px;border-radius:8px;font-weight:600;font-size:16px;box-shadow:0 4px 12px rgba(25,118,210,0.3);transition:all 0.2s ease;'>" +
                (contract.getIsFullySigned() ? "Télécharger le contrat" : "Signer le contrat") +
                "</a>" +
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
    }

    private SignatureResponseDto mapToResponseDto(Contract contract) {
        SignatureResponseDto dto = new SignatureResponseDto();
        dto.setContractId(contract.getId());
        dto.setClientSignature(contract.getClientSignature());
        dto.setInsurerSignature(contract.getInsurerSignature());
        dto.setClientSignedAt(contract.getClientSignedAt());
        dto.setInsurerSignedAt(contract.getInsurerSignedAt());
        dto.setIsFullySigned(contract.getIsFullySigned());
        
        if (Boolean.TRUE.equals(contract.getIsFullySigned())) {
            dto.setMessage("Contrat entièrement signé");
        } else if (contract.getClientSignature() != null) {
            dto.setMessage("En attente de signature de l'assureur");
        } else {
            dto.setMessage("En attente de signature du client");
        }
        
        return dto;
    }
}
