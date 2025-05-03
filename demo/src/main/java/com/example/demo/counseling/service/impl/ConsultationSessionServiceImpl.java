package com.example.demo.counseling.service.impl;

import com.example.demo.counseling.model.entity.ConsultationSession;
import com.example.demo.counseling.model.enums.SessionStatus;
import com.example.demo.counseling.repository.ConsultationSessionRepository;
import com.example.demo.counseling.service.ConsultationSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class ConsultationSessionServiceImpl implements ConsultationSessionService {

    @Autowired
    private ConsultationSessionRepository sessionRepository;

    @Override
    public void updateSessionStatus(Long sessionId, SessionStatus status) {
        sessionRepository.updateStatus(sessionId, status);
    }

    @Override
    public void updateLastMessageTime(Long sessionId) {
        sessionRepository.updateLastMessageTime(sessionId, LocalDateTime.now());
    }

    @Override
    public void endSession(Long sessionId) {
        ConsultationSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setSessionEndTime(LocalDateTime.now());
        session.setSessionStatus(SessionStatus.COMPLETED);
        sessionRepository.save(session);
    }
}