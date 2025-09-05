package com.pfe.notifications.controllers;

import com.pfe.notifications.dto.NotificationRequest;
import com.pfe.notifications.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Stockage temporaire des notifications (en production, utiliser Redis ou base de données)
    private static final Map<String, List<Map<String, Object>>> userNotifications = new ConcurrentHashMap<>();

    @PostMapping("/send")
    @PreAuthorize("isAuthenticated()")
    public String send(@RequestBody NotificationRequest request) {
        notificationService.sendEmail(request);
        return "Notification sent!";
    }

    @PostMapping("/store")
    @PreAuthorize("isAuthenticated()")
    public String storeNotification(@RequestBody Map<String, Object> notification) {
        String userId = (String) notification.get("userId");
        if (userId != null) {
            userNotifications.computeIfAbsent(userId, k -> new ArrayList<>()).add(notification);
        }
        return "Notification stored!";
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public List<Map<String, Object>> getUserNotifications(@PathVariable String userId) {
        return userNotifications.getOrDefault(userId, new ArrayList<>());
    }

    @DeleteMapping("/user/{userId}/{notificationId}")
    @PreAuthorize("isAuthenticated()")
    public String deleteNotification(@PathVariable String userId, @PathVariable String notificationId) {
        List<Map<String, Object>> notifications = userNotifications.get(userId);
        if (notifications != null) {
            notifications.removeIf(n -> notificationId.equals(n.get("id").toString()));
        }
        return "Notification deleted!";
    }
}