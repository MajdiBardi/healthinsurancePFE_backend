package com.pfe.contract.services;

import com.pfe.contract.clients.NotificationClient;
import com.pfe.contract.dtos.ChangeRequestCreateDto;
import com.pfe.contract.dtos.ChangeRequestResponseDto;
import com.pfe.contract.dtos.NotificationEmailRequest;
import com.pfe.contract.entities.ChangeRequest;
import com.pfe.contract.entities.Contract;
import com.pfe.contract.repositories.ChangeRequestRepository;
import com.pfe.contract.repositories.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChangeRequestServiceImpl implements ChangeRequestService {

    private final ChangeRequestRepository changeRequestRepository;
    private final ContractRepository contractRepository;
    private final NotificationClient notificationClient;

    @Override
    public ChangeRequestResponseDto submitRequest(String requesterId, ChangeRequestCreateDto dto) {
        Contract contract = contractRepository.findById(dto.getContractId())
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        ChangeRequest request = ChangeRequest.builder()
                .contract(contract)
                .requesterId(requesterId)
                .changeType(dto.getChangeType())
                .requestedChanges(dto.getDescription())
                .status("PENDING")
                .requestedAt(LocalDateTime.now())
                .build();

        ChangeRequest saved = changeRequestRepository.save(request);

        // Notify admin/insurer - using a generic address or system email logic
        NotificationEmailRequest email = new NotificationEmailRequest();
        email.setTo("admin@system.local");
        email.setSubject("New contract change request");
        email.setBody("Change request #" + saved.getId() + " for contract #" + contract.getId());
        try { notificationClient.sendEmail(email); } catch (Exception ignored) {}

        return mapToDto(saved);
    }

    @Override
    public List<ChangeRequestResponseDto> listMyRequests(String requesterId) {
        return changeRequestRepository.findByRequesterId(requesterId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<ChangeRequestResponseDto> listPending() {
        return changeRequestRepository.findByStatus("PENDING")
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public ChangeRequestResponseDto approve(Long requestId, String approverId) {
        ChangeRequest req = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        req.setStatus("APPROVED");
        req.setDecidedBy(approverId);
        req.setDecidedAt(LocalDateTime.now());
        ChangeRequest saved = changeRequestRepository.save(req);

        // Envoyer notification email
        NotificationEmailRequest email = new NotificationEmailRequest();
        email.setTo("client@system.local");
        email.setSubject("Your change request has been approved");
        email.setBody("Request #" + saved.getId() + " approved.");
        try { notificationClient.sendEmail(email); } catch (Exception ignored) {}

        // Envoyer notification en temps réel
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("id", System.currentTimeMillis());
            notification.put("userId", req.getRequesterId());
            notification.put("type", "CHANGE_REQUEST_UPDATE");
            notification.put("title", "Demande approuvée");
            notification.put("message", "Votre demande de modification pour le contrat #" + req.getContract().getId() + " a été approuvée.");
            notification.put("contractId", req.getContract().getId());
            notification.put("requestId", req.getId());
            notification.put("status", "APPROVED");
            notification.put("timestamp", LocalDateTime.now().toString());
            notification.put("read", false);
            
            notificationClient.storeNotification(notification);
        } catch (Exception ignored) {}

        return mapToDto(saved);
    }

    @Override
    public ChangeRequestResponseDto reject(Long requestId, String approverId) {
        ChangeRequest req = changeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        req.setStatus("REJECTED");
        req.setDecidedBy(approverId);
        req.setDecidedAt(LocalDateTime.now());
        ChangeRequest saved = changeRequestRepository.save(req);

        // Envoyer notification email
        NotificationEmailRequest email = new NotificationEmailRequest();
        email.setTo("client@system.local");
        email.setSubject("Your change request has been rejected");
        email.setBody("Request #" + saved.getId() + " rejected.");
        try { notificationClient.sendEmail(email); } catch (Exception ignored) {}

        // Envoyer notification en temps réel
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("id", System.currentTimeMillis());
            notification.put("userId", req.getRequesterId());
            notification.put("type", "CHANGE_REQUEST_UPDATE");
            notification.put("title", "Demande rejetée");
            notification.put("message", "Votre demande de modification pour le contrat #" + req.getContract().getId() + " a été rejetée.");
            notification.put("contractId", req.getContract().getId());
            notification.put("requestId", req.getId());
            notification.put("status", "REJECTED");
            notification.put("timestamp", LocalDateTime.now().toString());
            notification.put("read", false);
            
            notificationClient.storeNotification(notification);
        } catch (Exception ignored) {}

        return mapToDto(saved);
    }

    private ChangeRequestResponseDto mapToDto(ChangeRequest entity) {
        ChangeRequestResponseDto dto = new ChangeRequestResponseDto();
        dto.setId(entity.getId());
        dto.setContractId(entity.getContract().getId());
        dto.setRequesterId(entity.getRequesterId());
        dto.setChangeType(entity.getChangeType());
        dto.setDescription(entity.getRequestedChanges());
        dto.setStatus(entity.getStatus());
        dto.setRequestedAt(entity.getRequestedAt());
        dto.setDecidedBy(entity.getDecidedBy());
        dto.setDecidedAt(entity.getDecidedAt());
        return dto;
    }
}




