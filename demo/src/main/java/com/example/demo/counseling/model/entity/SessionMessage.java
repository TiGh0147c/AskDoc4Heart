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
    @Column(name = "message_id")
    private Long messageId;
    @Column(name = "session_id")
    private Long sessionId;

    @Enumerated(EnumType.STRING)
    private SenderRole senderRole;

    @Column(name = "sender_id")
    private Long senderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType;

    @Column(name = "message_content")
    private String messageContent;
    @Column(name = "message_sent_time")
    private LocalDateTime messageSentTime;
}
