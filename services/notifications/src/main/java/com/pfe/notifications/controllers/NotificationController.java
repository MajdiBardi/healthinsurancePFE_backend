package com.pfe.notifications.controllers;

import com.pfe.notifications.dto.NotificationRequest;
import com.pfe.notifications.services.NotificationService;
import com.pfe.notifications.dto.NotificationRequest;
import com.pfe.notifications.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public String send(@RequestBody NotificationRequest request) {
        notificationService.sendEmail(request);
        return "Notification sent!";
    }
}
