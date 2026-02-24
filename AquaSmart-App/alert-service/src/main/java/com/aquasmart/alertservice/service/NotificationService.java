package com.aquasmart.alertservice.service;

import com.aquasmart.alertservice.model.Alert;
import com.aquasmart.alertservice.model.NotificationChannel;

public interface NotificationService {
    
    void sendNotification(Alert alert);
    
    void sendEmail(Long userId, String email, String subject, String content, Alert alert);
    
    void sendSms(Long userId, String phoneNumber, String content, Alert alert);
    
    void sendPushNotification(Long userId, String title, String message, Alert alert);
    
    void retryFailedNotifications();
}
