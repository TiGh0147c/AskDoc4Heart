package com.example.demo.counseling.model.entity;

import com.example.demo.counseling.model.enums.MessageType;
import com.example.demo.counseling.model.enums.SenderRole;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "session_message")
public class SessionMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    private Long sessionId;

    @Enumerated(EnumType.STRING)
    private SenderRole senderRole;

    private Long senderId;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    private String messageContent;
    private LocalDateTime messageSentTime;
}
