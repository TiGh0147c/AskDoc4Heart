package com.example.demo.Message.Repository;

import com.example.demo.Message.Entity.SessionMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<SessionMessage, Integer> {

    // 分页查询会话消息
    Page<SessionMessage> findBySessionId(Integer sessionId, Pageable pageable);

    // 获取会话最后一条消息
    Optional<SessionMessage> findTopBySessionIdOrderByMessageSentTimeDesc(Integer sessionId);

    // 统计未读消息数量
    Long countBySessionIdAndSenderRoleAndMessageSentTimeAfter(
            Integer sessionId,
            SessionMessage.SenderRole senderRole,
            LocalDateTime lastReadTime);
}