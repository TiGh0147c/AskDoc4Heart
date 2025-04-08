package com.example.demo.Message.Repository;

import com.example.demo.Message.Entity.ConsultationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessionRepository extends
        JpaRepository<ConsultationSession, Integer>,
        JpaSpecificationExecutor<ConsultationSession> {

    // 根据最后消息时间查找活跃会话
    List<ConsultationSession> findByLastMessageSentTimeAfter(LocalDateTime time);

    // 检查用户是否有进行中的会话
    boolean existsByUserIdAndSessionStatus(Integer userId, ConsultationSession.SessionStatus status);
}
