package com.example.demo.counseling.repository;

import com.example.demo.counseling.model.entity.ConsultationSession;
import com.example.demo.counseling.model.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultationSessionRepository extends JpaRepository<ConsultationSession, Long> {
    
    @Modifying
    @Transactional
    @Query("UPDATE ConsultationSession s SET s.sessionStatus = :status WHERE s.sessionId = :sessionId")
    void updateSessionStatus(@Param("sessionId") Long sessionId, @Param("status") SessionStatus status);
    
    @Modifying
    @Transactional
    @Query("UPDATE ConsultationSession s SET s.lastMessageSentTime = :time WHERE s.sessionId = :sessionId")
    void updateLastMessageTime(@Param("sessionId") Long sessionId, @Param("time") LocalDateTime time);
    
    List<ConsultationSession> findByCounselorIdAndSessionStatusNot(Long counselorId, SessionStatus status);

    List<ConsultationSession> findByCounselorIdAndSessionStatus(Long counselorId, SessionStatus sessionStatus);

    List<ConsultationSession> findByUserIdAndSessionStatus(Long userId, SessionStatus sessionStatus);
    
    // 新增方法
    List<ConsultationSession> findByUserIdAndSessionStatusNot(Long userId, SessionStatus status);
    
    List<ConsultationSession> findByUserIdAndCounselorIdAndSessionStatusNot(Long userId, Long counselorId, SessionStatus status);
}