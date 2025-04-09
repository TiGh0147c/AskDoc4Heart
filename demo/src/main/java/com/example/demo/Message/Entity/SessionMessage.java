package com.example.demo.Message.Entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// SessionMessage.java
@Data
@Entity
@Table(name = "Session_Message")
public class SessionMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    @Column(name = "session_id", nullable = false)
    private Integer sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_role", nullable = false)
    private SenderRole senderRole;

    @Column(name = "sender_id", nullable = false)
    private Integer senderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @Column(name = "message_content")
    private String messageContent;

    @Column(name = "message_sent_time", nullable = false)
    private LocalDateTime messageSentTime = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private ConsultationSession session;

    public enum SenderRole {
        USER, COUNSELOR
    }

    public enum MessageType {
        TEXT, IMAGE, FILE
    }
}