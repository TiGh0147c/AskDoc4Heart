package com.example.demo.counseling.service.impl;

import com.example.demo.counseling.model.entity.SessionMessage;
import com.example.demo.counseling.repository.SessionMessageRepository;
import com.example.demo.counseling.service.SessionMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionMessageServiceImpl implements SessionMessageService {

    @Autowired
    private SessionMessageRepository messageRepository;
    
    @Override
    public SessionMessage saveMessage(SessionMessage message) {
        return messageRepository.save(message);
    }
    
    @Override
    public List<SessionMessage> getMessagesBySessionId(Long sessionId) {
        return messageRepository.findBySessionIdOrderByMessageSentTimeAsc(sessionId);
    }
    
    @Override
    public SessionMessage getLastMessageBySessionId(Long sessionId) {
        return messageRepository.findTopBySessionIdOrderByMessageSentTimeDesc(sessionId);
    }
}