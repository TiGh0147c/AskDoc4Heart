package com.example.demo.counseling.controller;

import com.example.demo.counseling.model.dto.WebSocketMessage;
import com.example.demo.counseling.model.entity.ConsultationSession;
import com.example.demo.counseling.model.entity.SessionMessage;
import com.example.demo.counseling.model.enums.MessageType;
import com.example.demo.counseling.model.enums.SenderRole;
import com.example.demo.counseling.model.enums.SessionStatus;
import com.example.demo.counseling.service.ConsultationSessionService;
import com.example.demo.counseling.service.SessionMessageService;
import com.example.demo.Appointment.mapper.AppointmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserConsultationSessionController {

    @Autowired
    private ConsultationSessionService sessionService;
    
    @Autowired
    private SessionMessageService messageService;
    
    @Autowired
    private AppointmentMapper appointmentMapper;

    // 获取用户的活跃会话列表
    @GetMapping("/chats")
    public ResponseEntity<?> getActiveSessions(@RequestParam Long userId) {
        // 查询用户的所有活跃会话
        List<ConsultationSession> sessions = sessionService.getActiveSessionsByUserId(userId);
        
        List<Map<String, Object>> result = sessions.stream().map(session -> {
            Map<String, Object> chatInfo = new HashMap<>();
            chatInfo.put("id", session.getSessionId());
            chatInfo.put("counselorId", session.getCounselorId());
            
            // 从Appointment包获取咨询师信息
            String counselorName = appointmentMapper.getCounselorNameByCounselorId(session.getCounselorId().intValue());
            chatInfo.put("counselorName", counselorName != null ? counselorName : "咨询师" + session.getCounselorId());
            chatInfo.put("counselorAvatar", "/basic_avatar/basic_male.jpg"); // 默认头像
            chatInfo.put("type", "心理咨询");
            
            // 获取最后一条消息
            SessionMessage lastMessage = messageService.getLastMessageBySessionId(session.getSessionId());
            if (lastMessage != null) {
                chatInfo.put("lastMessage", lastMessage.getMessageContent());
                
                // 格式化时间为 HH:mm
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                chatInfo.put("lastMessageTime", lastMessage.getMessageSentTime().format(formatter));
            } else {
                chatInfo.put("lastMessage", "暂无消息");
                chatInfo.put("lastMessageTime", "");
            }
            
            // 未读消息数量，默认为0
            chatInfo.put("unreadCount", 0);
            
            return chatInfo;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }

    // 获取或创建与特定咨询师的会话
    @GetMapping("/chats/counselor/{counselorId}")
    public ResponseEntity<?> getOrCreateSession(
            @PathVariable Long counselorId,
            @RequestParam Long userId) {
        
        // 查找是否已存在活跃会话
        List<ConsultationSession> existingSessions = sessionService.getActiveSessionsByUserIdAndCounselorId(userId, counselorId);
        
        ConsultationSession session;
        boolean isNewSession = false;
        
        if (existingSessions.isEmpty()) {
            // 创建新会话
            session = new ConsultationSession();
            session.setCounselorId(counselorId);
            session.setUserId(userId);
            session.setSessionStatus(SessionStatus.IN_PROGRESS);
            session.setSessionStartTime(LocalDateTime.now());
            session.setLastMessageSentTime(LocalDateTime.now());
            session.setUserHasEvaluated(false);
            session.setCounselorHasEvaluated(false);
            
            Long sessionId = sessionService.createSession(session);
            session.setSessionId(sessionId);
            isNewSession = true;
        } else {
            // 使用现有会话
            session = existingSessions.get(0);
        }
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", session.getSessionId());
        
        // 获取会话消息
        List<SessionMessage> messageEntities = messageService.getMessagesBySessionId(session.getSessionId());
        
        List<Map<String, Object>> messages = messageEntities.stream().map(msg -> {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("sender", msg.getSenderRole() == SenderRole.USER ? "user" : "counselor");
            messageMap.put("text", msg.getMessageContent());
            
            // 格式化时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            messageMap.put("time", msg.getMessageSentTime().format(formatter));
            
            return messageMap;
        }).collect(Collectors.toList());
        
        response.put("messages", messages);
        response.put("isNewSession", isNewSession);
        
        return ResponseEntity.ok(response);
    }
}