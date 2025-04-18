package com.example.demo.Evaluation.Entity;

import jakarta.persistence.*;

@Entity
@Table(name="Consultation_Session")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="session_id")
    private int sessionId;
    @Column(name="user_id")
    private int userId;
    @Column(name="counselor_id")
    private int counselorId;
    @Column(name="supervisor_id")
    private int supervisorId;

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCounselorId() {
        return counselorId;
    }

    public void setCounselorId(int counselorId) {
        this.counselorId = counselorId;
    }

    public int getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(int supervisorId) {
        this.supervisorId = supervisorId;
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionId=" + sessionId +
                ", userId=" + userId +
                ", counselorId=" + counselorId +
                ", supervisorId=" + supervisorId +
                '}';
    }
}
