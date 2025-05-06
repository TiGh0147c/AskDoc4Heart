package com.example.demo.counseling.service;

import com.example.demo.counseling.model.entity.ConsultationSession;
import com.example.demo.counseling.model.enums.SessionStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultationSessionService {
    void updateSessionStatus(Long sessionId, SessionStatus status);
    void updateLastMessageTime(Long sessionId);
    void endSession(Long sessionId);
    Long createSession(ConsultationSession session);
    ConsultationSession getSessionById(Long sessionId);
    List<ConsultationSession> getActiveSessionsByCounselorId(Long counselorId);
    
    // 新增方法
    List<ConsultationSession> getActiveSessionsByUserId(Long userId);
    List<ConsultationSession> getActiveSessionsByUserIdAndCounselorId(Long userId, Long counselorId);

    List<ConsultationSession> getCompletedSessionsByCounselorId(Long counselorId);
    List<ConsultationSession> getCompletedSessionsByUserId(Long userId);

}