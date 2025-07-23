package com.SMailAI.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class EmailSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipientEmail;
    private String subject;

    @Lob
    private String body;

    private LocalDateTime scheduledTime;
    private boolean sent;

    // 👉 Getters
    public Long getId() {
        return id;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public boolean isSent() {
        return sent;
    }

    // 👉 Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
