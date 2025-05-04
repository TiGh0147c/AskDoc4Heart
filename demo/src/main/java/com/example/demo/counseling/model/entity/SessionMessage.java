package com.example.demo.counseling.model.entity;

import com.example.demo.counseling.model.enums.MessageType;
import com.example.demo.counseling.model.enums.SenderRole;
import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "session_message")
public class SessionMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;
    
    @Column(nullable = false)
    private Long sessionId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SenderRole senderRole;
    
    @Column(nullable = false)
    private Long senderId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String messageContent;
    
    @Column(nullable = false)
    private LocalDateTime messageSentTime;
    
    // 移除了counselorRead和userRead字段
}
