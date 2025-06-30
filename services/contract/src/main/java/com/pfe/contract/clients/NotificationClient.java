package com.pfe.contract.clients;

import com.pfe.contract.dtos.NotificationEmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notifications")
public interface NotificationClient {

    @PostMapping("/api/notifications/send")  // Mettre à jour le chemin
    void sendEmail(@RequestBody NotificationEmailRequest request);
}