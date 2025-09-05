package com.pfe.contract.clients;

import com.pfe.contract.dtos.NotificationEmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "notifications")
public interface NotificationClient {

    @PostMapping("/api/notifications/send")
    void sendEmail(@RequestBody NotificationEmailRequest request);

    @PostMapping("/api/notifications/store")
    void storeNotification(@RequestBody Map<String, Object> notification);
}