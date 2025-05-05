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
    @Column(name = "session_id")
    private Long sessionId;
    @Column(name = "session_start_time")
    private LocalDateTime sessionStartTime;
    @Column(name = "session_end_time")
    private LocalDateTime sessionEndTime;

    @Enumerated(EnumType.STRING)
    private SessionStatus sessionStatus;
    @Column(name = "counselor_id")
    private Long counselorId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "last_message_sent_time")
    private LocalDateTime lastMessageSentTime;
    @Column(name = "user_has_evaluated")
    private Boolean userHasEvaluated;
    @Column(name = "counselor_has_evaluated")
    private Boolean counselorHasEvaluated;
}