package com.example.demo.counseling.service.impl;

import com.example.demo.counseling.model.entity.ConsultationSession;
import com.example.demo.counseling.model.enums.SessionStatus;
import com.example.demo.counseling.repository.ConsultationSessionRepository;
import com.example.demo.counseling.service.ConsultationSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultationSessionServiceImpl implements ConsultationSessionService {

    @Autowired
    private ConsultationSessionRepository sessionRepository;

    @Override
    public void updateSessionStatus(Long sessionId, SessionStatus status) {
        sessionRepository.updateSessionStatus(sessionId, status);
    }

    @Override
    public void updateLastMessageTime(Long sessionId) {
        sessionRepository.updateLastMessageTime(sessionId, LocalDateTime.now());
    }

    @Override
    public void endSession(Long sessionId) {
        ConsultationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        session.setSessionStatus(SessionStatus.COMPLETED);
        session.setSessionEndTime(LocalDateTime.now());
        sessionRepository.save(session);
    }

    @Override
    public Long createSession(ConsultationSession session) {
        ConsultationSession savedSession = sessionRepository.save(session);
        return savedSession.getSessionId();
    }

    @Override
    public ConsultationSession getSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }

    @Override
    public List<ConsultationSession> getActiveSessionsByCounselorId(Long counselorId) {
        return sessionRepository.findByCounselorIdAndSessionStatusNot(counselorId, SessionStatus.COMPLETED);
    }
    
    @Override
    public List<ConsultationSession> getActiveSessionsByUserId(Long userId) {
        return sessionRepository.findByUserIdAndSessionStatusNot(userId, SessionStatus.COMPLETED);
    }
    
    @Override
    public List<ConsultationSession> getActiveSessionsByUserIdAndCounselorId(Long userId, Long counselorId) {
        return sessionRepository.findByUserIdAndCounselorIdAndSessionStatusNot(userId, counselorId, SessionStatus.COMPLETED);
    }
}