package com.example.demo.counseling.handler;

import com.example.demo.counseling.model.dto.WebSocketMessage;
import com.example.demo.counseling.model.dto.WebSocketResponse;
import com.example.demo.counseling.model.entity.SessionMessage;
import com.example.demo.counseling.model.enums.SessionStatus;
import com.example.demo.counseling.repository.SessionMessageRepository;
import com.example.demo.counseling.service.ConsultationSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConsultationHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ConsultationHandler.class);
    private static final Map<Long, Map<String, WebSocketSession>> sessionMap = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ConsultationSessionService sessionService;
    @Autowired
    private SessionMessageRepository messageRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long sessionId = getSessionIdFromAttributes(session);
        String participantId = getParticipantIdFromAttributes(session);

        if (sessionId == null || participantId == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        sessionMap.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>())
                .put(participantId, session);

        sessionService.updateSessionStatus(sessionId, SessionStatus.IN_PROGRESS);
        logger.info("Participant {} joined consultation session {}", participantId, sessionId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long sessionId = getSessionIdFromAttributes(session);
        String participantId = getParticipantIdFromAttributes(session);

        try {
            WebSocketMessage msg = objectMapper.readValue(message.getPayload(), WebSocketMessage.class);

            SessionMessage entity = new SessionMessage();
            entity.setSessionId(sessionId);
            entity.setSenderRole(msg.getSenderRole());
            entity.setSenderId(msg.getSenderId());
            entity.setMessageType(msg.getType());
            entity.setMessageContent(msg.getContent());
            entity.setMessageSentTime(LocalDateTime.now());
            messageRepository.save(entity);

            sessionService.updateLastMessageTime(sessionId);
            forwardMessage(sessionId, participantId, message.getPayload());

        } catch (Exception e) {
            logger.error("Error handling message", e);
            session.sendMessage(new TextMessage((CharSequence) WebSocketResponse.error("消息处理失败")));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long sessionId = getSessionIdFromAttributes(session);
        String participantId = getParticipantIdFromAttributes(session);

        if (sessionId != null && participantId != null) {
            sessionMap.getOrDefault(sessionId, Collections.emptyMap())
                    .remove(participantId);

            if (sessionMap.getOrDefault(sessionId, Collections.emptyMap()).isEmpty()) {
                sessionService.endSession(sessionId);
                sessionMap.remove(sessionId);
            }
        }
    }

    private void forwardMessage(Long sessionId, String senderId, String message) throws IOException {
        Map<String, WebSocketSession> participants = sessionMap.get(sessionId);
        if (participants == null) return;

        for (Map.Entry<String, WebSocketSession> entry : participants.entrySet()) {
            if (!entry.getKey().equals(senderId)) {
                WebSocketSession targetSession = entry.getValue();
                if (targetSession.isOpen()) {
                    targetSession.sendMessage(new TextMessage(message));
                }
            }
        }
    }

    private Long getSessionIdFromAttributes(WebSocketSession session) {
        return (Long) session.getAttributes().get("sessionId");
    }

    private String getParticipantIdFromAttributes(WebSocketSession session) {
        return (String) session.getAttributes().get("participantId");
    }
}