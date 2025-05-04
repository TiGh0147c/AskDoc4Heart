package com.example.demo.counseling.service;

import com.example.demo.counseling.model.entity.SessionMessage;

import java.util.List;

public interface SessionMessageService {
    
    /**
     * 保存消息
     */
    SessionMessage saveMessage(SessionMessage message);
    
    /**
     * 获取会话的所有消息
     */
    List<SessionMessage> getMessagesBySessionId(Long sessionId);
    
    /**
     * 获取会话的最后一条消息
     */
    SessionMessage getLastMessageBySessionId(Long sessionId);
    
    // 移除了与counselorRead相关的方法
}