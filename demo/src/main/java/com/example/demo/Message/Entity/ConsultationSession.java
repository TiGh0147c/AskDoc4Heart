package com.example.demo.Message.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// ConsultationSession.java
@Data
@Entity
@Table(name = "Consultation_Session")
public class ConsultationSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sessionId;

    @Column(name = "session_start_time", nullable = false)
    private LocalDateTime sessionStartTime;

    @Column(name = "session_end_time")
    private LocalDateTime sessionEndTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_status", nullable = false)
    private SessionStatus sessionStatus;

    @Column(name = "counselor_id", nullable = false)
    private Integer counselorId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "supervisor_id", nullable = false)
    private Integer supervisorId;

    @Column(name = "last_message_sent_time")
    private LocalDateTime lastMessageSentTime;

    @Column(name = "user_has_evaluated", nullable = false)
    private Boolean userHasEvaluated = false;

    @Column(name = "counselor_has_evaluated", nullable = false)
    private Boolean counselorHasEvaluated = false;

    //@OneToOne(mappedBy = "session", cascade = CascadeType.ALL)
    //private SessionSummary summary;

    public enum SessionStatus {
        IN_PROGRESS, COMPLETED
    }
}