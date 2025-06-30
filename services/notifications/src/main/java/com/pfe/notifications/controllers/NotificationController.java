package com.pfe.notifications.controllers;

import com.pfe.notifications.dto.NotificationRequest;
import com.pfe.notifications.services.NotificationService;
import com.pfe.notifications.dto.NotificationRequest;
import com.pfe.notifications.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    @PreAuthorize("isAuthenticated()")  // ou une règle plus spécifique selon vos besoins
    public String send(@RequestBody NotificationRequest request) {
        notificationService.sendEmail(request);
        return "Notification sent!";
    }
}