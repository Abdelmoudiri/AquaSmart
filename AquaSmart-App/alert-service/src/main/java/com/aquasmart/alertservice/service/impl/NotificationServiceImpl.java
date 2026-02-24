package com.aquasmart.alertservice.service.impl;

import com.aquasmart.alertservice.model.*;
import com.aquasmart.alertservice.repository.AlertPreferenceRepository;
import com.aquasmart.alertservice.repository.NotificationRepository;
import com.aquasmart.alertservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final AlertPreferenceRepository preferenceRepository;
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username:noreply@aquasmart.com}")
    private String fromEmail;
    
    @Value("${alert.notification.email-enabled:true}")
    private boolean emailEnabled;
    
    @Value("${alert.notification.sms-enabled:false}")
    private boolean smsEnabled;
    
    @Override
    @Async
    public void sendNotification(Alert alert) {
        log.info("Sending notification for alert: {} - Type: {}", alert.getId(), alert.getType());
        
        AlertPreference preference = preferenceRepository.findByUserId(alert.getUserId())
                .orElse(createDefaultPreference(alert.getUserId()));
        
        // Check quiet hours
        if (isQuietHours(preference)) {
            log.info("Quiet hours active for user: {} - skipping notification", alert.getUserId());
            return;
        }
        
        // Send in-app notification (always)
        if (preference.getInAppEnabled()) {
            saveNotification(alert, NotificationChannel.IN_APP, null, alert.getTitle(), alert.getMessage(), true);
        }
        
        // Send email if enabled and severity meets threshold
        if (emailEnabled && preference.getEmailEnabled() && 
            preference.getEmail() != null && 
            meetsMinSeverity(alert.getSeverity(), preference.getEmailMinSeverity())) {
            sendEmail(alert.getUserId(), preference.getEmail(), 
                     buildEmailSubject(alert), buildEmailContent(alert), alert);
        }
        
        // Send SMS if enabled and severity meets threshold
        if (smsEnabled && preference.getSmsEnabled() && 
            preference.getPhoneNumber() != null &&
            meetsMinSeverity(alert.getSeverity(), preference.getSmsMinSeverity())) {
            sendSms(alert.getUserId(), preference.getPhoneNumber(), 
                   buildSmsContent(alert), alert);
        }
    }
    
    @Override
    @Transactional
    public void sendEmail(Long userId, String email, String subject, String content, Alert alert) {
        log.info("Sending email to: {} for alert: {}", email, alert.getId());
        
        Notification notification = Notification.builder()
                .alert(alert)
                .userId(userId)
                .channel(NotificationChannel.EMAIL)
                .recipient(email)
                .subject(subject)
                .content(content)
                .build();
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            
            notification.setSent(true);
            notification.setSentAt(LocalDateTime.now());
            log.info("Email sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send email to: {} - Error: {}", email, e.getMessage());
            notification.setSent(false);
            notification.setErrorMessage(e.getMessage());
            notification.setRetryCount(notification.getRetryCount() + 1);
        }
        
        notificationRepository.save(notification);
    }
    
    @Override
    @Transactional
    public void sendSms(Long userId, String phoneNumber, String content, Alert alert) {
        log.info("Sending SMS to: {} for alert: {}", phoneNumber, alert.getId());
        
        Notification notification = Notification.builder()
                .alert(alert)
                .userId(userId)
                .channel(NotificationChannel.SMS)
                .recipient(phoneNumber)
                .content(content)
                .build();
        
        // TODO: Integrate with SMS provider (Twilio, etc.)
        // For now, just log and mark as sent
        log.info("SMS notification (simulated) to: {} - Content: {}", phoneNumber, content);
        notification.setSent(true);
        notification.setSentAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
    
    @Override
    @Transactional
    public void sendPushNotification(Long userId, String title, String message, Alert alert) {
        log.info("Sending push notification to user: {} for alert: {}", userId, alert.getId());
        
        Notification notification = Notification.builder()
                .alert(alert)
                .userId(userId)
                .channel(NotificationChannel.PUSH)
                .subject(title)
                .content(message)
                .build();
        
        // TODO: Integrate with push notification service (Firebase, etc.)
        log.info("Push notification (simulated) to user: {} - Title: {}", userId, title);
        notification.setSent(true);
        notification.setSentAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
    
    @Override
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    @Transactional
    public void retryFailedNotifications() {
        List<Notification> failedNotifications = notificationRepository.findFailedNotifications();
        
        for (Notification notification : failedNotifications) {
            log.info("Retrying notification: {} - Channel: {}", 
                    notification.getId(), notification.getChannel());
            
            if (notification.getChannel() == NotificationChannel.EMAIL) {
                try {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setFrom(fromEmail);
                    message.setTo(notification.getRecipient());
                    message.setSubject(notification.getSubject());
                    message.setText(notification.getContent());
                    
                    mailSender.send(message);
                    
                    notification.setSent(true);
                    notification.setSentAt(LocalDateTime.now());
                    log.info("Retry successful for notification: {}", notification.getId());
                } catch (Exception e) {
                    notification.setRetryCount(notification.getRetryCount() + 1);
                    notification.setErrorMessage(e.getMessage());
                    log.error("Retry failed for notification: {} - Error: {}", 
                             notification.getId(), e.getMessage());
                }
                
                notificationRepository.save(notification);
            }
        }
    }
    
    private void saveNotification(Alert alert, NotificationChannel channel, String recipient,
                                   String subject, String content, boolean sent) {
        Notification notification = Notification.builder()
                .alert(alert)
                .userId(alert.getUserId())
                .channel(channel)
                .recipient(recipient)
                .subject(subject)
                .content(content)
                .sent(sent)
                .sentAt(sent ? LocalDateTime.now() : null)
                .build();
        
        notificationRepository.save(notification);
    }
    
    private boolean isQuietHours(AlertPreference preference) {
        if (preference.getQuietHoursStart() == null || preference.getQuietHoursEnd() == null) {
            return false;
        }
        
        int currentHour = LocalTime.now().getHour();
        int start = preference.getQuietHoursStart();
        int end = preference.getQuietHoursEnd();
        
        if (start <= end) {
            return currentHour >= start && currentHour < end;
        } else {
            // Quiet hours span midnight
            return currentHour >= start || currentHour < end;
        }
    }
    
    private boolean meetsMinSeverity(AlertSeverity alertSeverity, AlertSeverity minSeverity) {
        if (minSeverity == null) return true;
        return alertSeverity.ordinal() >= minSeverity.ordinal();
    }
    
    private String buildEmailSubject(Alert alert) {
        String severityPrefix = switch (alert.getSeverity()) {
            case EMERGENCY -> "🚨 URGENCE";
            case CRITICAL -> "⚠️ CRITIQUE";
            case WARNING -> "⚡ ALERTE";
            case INFO -> "ℹ️ INFO";
        };
        return String.format("[AquaSmart] %s: %s", severityPrefix, alert.getTitle());
    }
    
    private String buildEmailContent(Alert alert) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== AquaSmart - Système d'Alerte ===\n\n");
        sb.append("Type: ").append(alert.getType()).append("\n");
        sb.append("Sévérité: ").append(alert.getSeverity()).append("\n");
        sb.append("Date: ").append(alert.getCreatedAt()).append("\n\n");
        sb.append("Titre: ").append(alert.getTitle()).append("\n");
        sb.append("Message: ").append(alert.getMessage()).append("\n\n");
        
        if (alert.getTriggerValue() != null) {
            sb.append("Valeur détectée: ").append(alert.getTriggerValue()).append("\n");
        }
        if (alert.getThresholdValue() != null) {
            sb.append("Seuil: ").append(alert.getThresholdValue()).append("\n");
        }
        if (alert.getRecommendedAction() != null) {
            sb.append("\nAction recommandée: ").append(alert.getRecommendedAction()).append("\n");
        }
        
        sb.append("\n---\n");
        sb.append("Connectez-vous à AquaSmart pour plus de détails.\n");
        
        return sb.toString();
    }
    
    private String buildSmsContent(Alert alert) {
        return String.format("[AquaSmart] %s: %s - %s", 
                alert.getSeverity(), alert.getTitle(), 
                alert.getMessage() != null && alert.getMessage().length() > 100 
                        ? alert.getMessage().substring(0, 100) + "..." 
                        : alert.getMessage());
    }
    
    private AlertPreference createDefaultPreference(Long userId) {
        return AlertPreference.builder()
                .userId(userId)
                .emailEnabled(true)
                .smsEnabled(false)
                .pushEnabled(true)
                .inAppEnabled(true)
                .emailMinSeverity(AlertSeverity.WARNING)
                .smsMinSeverity(AlertSeverity.CRITICAL)
                .build();
    }
}
