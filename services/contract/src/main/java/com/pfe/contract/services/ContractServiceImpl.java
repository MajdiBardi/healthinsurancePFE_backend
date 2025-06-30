package com.pfe.contract.services;

import com.pfe.contract.clients.NotificationClient;
import com.pfe.contract.clients.UserClient;
import com.pfe.contract.dtos.ContractResponseDto;
import com.pfe.contract.dtos.NotificationEmailRequest;
import com.pfe.contract.dtos.UserDto;
import com.pfe.contract.entities.Contract;
import com.pfe.contract.repositories.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final NotificationClient notificationClient;
    private final UserClient userClient; // ✅ Injecté ici

    @Override
    public Contract createContract(Contract contract) {
        contract.setCreationDate(LocalDate.now());
        Contract saved = contractRepository.save(contract);
        sendNotificationEmail(saved); // Envoi email avec vraie adresse
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

    // ✅ Met à jour dynamiquement le statut
    private void updateStatusIfExpired(Contract contract) {
        LocalDate now = LocalDate.now();
        if (contract.getEndDate() != null) {
            contract.setStatus(now.isBefore(contract.getEndDate()) ? "ACTIVE" : "INACTIVE");
        }
    }

    // ✅ Appel réel au UserService pour récupérer l’email
    private void sendNotificationEmail(Contract contract) {
        UserDto user = userClient.getUserById(contract.getClientId());

        NotificationEmailRequest request = new NotificationEmailRequest();
        request.setTo(user.getEmail()); // ✅ Email réel récupéré dynamiquement
        request.setSubject("Nouveau contrat créé");
        request.setBody("Votre contrat a été créé et prendra fin le " + contract.getEndDate() + ".");
        notificationClient.sendEmail(request);
    }
}
