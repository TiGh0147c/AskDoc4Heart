package com.example.demo.counseling.repository;

import com.example.demo.counseling.model.entity.SessionMessage;
import com.example.demo.counseling.model.enums.SenderRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionMessageRepository extends JpaRepository<SessionMessage, Long> {
    
    // 按发送时间升序获取会话的所有消息
    List<SessionMessage> findBySessionIdOrderByMessageSentTimeAsc(Long sessionId);
    
    // 获取会话的最后一条消息
    SessionMessage findTopBySessionIdOrderByMessageSentTimeDesc(Long sessionId);
    
    // 移除了与counselorRead相关的方法
}