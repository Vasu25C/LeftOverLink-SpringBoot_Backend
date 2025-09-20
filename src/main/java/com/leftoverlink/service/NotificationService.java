package com.leftoverlink.service;

import com.leftoverlink.model.Notification;
import com.leftoverlink.model.NotificationType;
import com.leftoverlink.model.User;

import java.util.List;

public interface NotificationService {
    void notifyUser(User user, String message, NotificationType type);
    List<Notification> getNotificationsForUser(String email);
    void markAsRead(Long notificationId);
    void deleteNotification(Long id);
}
