package com.leftoverlink.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type") // ✅ avoid reserved word
    private NotificationType type;

    @Column(name = "is_read") // ✅ avoid reserved word
    private boolean read;

    @Column(name = "created_at") // ✅ avoid reserved word
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Notification() {
    }

    public Notification(Long id, String message, NotificationType type, boolean read, LocalDateTime timestamp, User user) {
        this.id = id;
        this.message = message;
        this.type = type;
        this.read = read;
        this.timestamp = timestamp;
        this.user = user;
    }

    // Builder pattern for convenience
    public static class Builder {
        private String message;
        private NotificationType type;
        private boolean read;
        private LocalDateTime timestamp;
        private User user;

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder type(NotificationType type) {
            this.type = type;
            return this;
        }

        public Builder read(boolean read) {
            this.read = read;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Notification build() {
            return new Notification(null, message, type, read, timestamp, user);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
