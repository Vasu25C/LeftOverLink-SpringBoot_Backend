package com.leftoverlink.controller;

import com.leftoverlink.model.Notification;
import com.leftoverlink.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    // ✅ Get notifications for the logged-in user
 // ✅ Get notifications for the logged-in user
    @GetMapping
    public ResponseEntity<List<Notification>> getUserNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername(); // Extract from JWT context
        return ResponseEntity.ok(notificationService.getNotificationsForUser(email));
    }

    // ✅ Mark a specific notification as read
    @PostMapping("/mark-read/{id}")
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("✅ Notification marked as read");
    }

    // ✅ Delete a specific notification
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("🗑 Notification deleted");
    }
}
