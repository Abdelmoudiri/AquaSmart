package com.aquasmart.alertservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Notification record - tracks sent notifications
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id")
    @ToString.Exclude
    private Alert alert;
    
    @Column(nullable = false)
    private Long userId;
    
    /**
     * Notification channel used
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;
    
    /**
     * Recipient address (email, phone, etc.)
     */
    private String recipient;
    
    /**
     * Subject (for email)
     */
    private String subject;
    
    /**
     * Message content
     */
    @Column(columnDefinition = "TEXT")
    private String content;
    
    /**
     * Was the notification sent successfully?
     */
    @Builder.Default
    private Boolean sent = false;
    
    /**
     * Error message if failed
     */
    private String errorMessage;
    
    /**
     * When the notification was created
     */
    private LocalDateTime createdAt;
    
    /**
     * When the notification was sent
     */
    private LocalDateTime sentAt;
    
    /**
     * Retry count
     */
    @Builder.Default
    private Integer retryCount = 0;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
