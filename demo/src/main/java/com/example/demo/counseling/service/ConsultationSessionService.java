package com.example.demo.counseling.service;

import com.example.demo.counseling.model.enums.SessionStatus;

public interface ConsultationSessionService {
    void updateSessionStatus(Long sessionId, SessionStatus status);
    void updateLastMessageTime(Long sessionId);
    void endSession(Long sessionId);
}