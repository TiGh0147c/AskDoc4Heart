package com.example.demo.counseling.repository;

import com.example.demo.counseling.model.entity.ConsultationSession;
import com.example.demo.counseling.model.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ConsultationSessionRepository extends JpaRepository<ConsultationSession, Long> {

    @Modifying
    @Query("UPDATE ConsultationSession cs SET cs.sessionStatus = :status WHERE cs.sessionId = :sessionId")
    void updateStatus(Long sessionId, SessionStatus status);

    @Modifying
    @Query("UPDATE ConsultationSession cs SET cs.lastMessageSentTime = :time WHERE cs.sessionId = :sessionId")
    void updateLastMessageTime(Long sessionId, LocalDateTime time);
}