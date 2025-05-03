package com.example.demo.counseling.model.entity;

import com.example.demo.counseling.model.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "consultation_session")
public class ConsultationSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;

    private LocalDateTime sessionStartTime;
    private LocalDateTime sessionEndTime;

    @Enumerated(EnumType.STRING)
    private SessionStatus sessionStatus;

    private Long counselorId;
    private Long userId;
    private Long supervisorId;
    private LocalDateTime lastMessageSentTime;
    private Boolean userHasEvaluated;
    private Boolean counselorHasEvaluated;
}