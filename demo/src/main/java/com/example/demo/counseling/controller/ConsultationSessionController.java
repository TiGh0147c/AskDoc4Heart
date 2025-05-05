package com.example.demo.counseling.controller;

import com.example.demo.counseling.model.dto.WebSocketMessage;
import com.example.demo.counseling.model.entity.ConsultationSession;
import com.example.demo.counseling.model.entity.SessionMessage;
import com.example.demo.counseling.model.enums.MessageType;
import com.example.demo.counseling.model.enums.SenderRole;
import com.example.demo.counseling.model.enums.SessionStatus;
import com.example.demo.counseling.service.ConsultationSessionService;
import com.example.demo.counseling.service.SessionMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/counselor")
public class ConsultationSessionController {

    @Autowired
    private ConsultationSessionService sessionService;
    
    @Autowired
    private SessionMessageService messageService;

    // 创建咨询会话
    @PostMapping("/consultation/sessions/create")
    public ResponseEntity<?> createSession(
            @RequestParam Long counselorId,
            @RequestParam Long userId) {
        
        // 创建新会话
        // 在创建会话的方法中
        ConsultationSession session = new ConsultationSession();
        session.setCounselorId(counselorId);
        session.setUserId(userId);
        session.setSessionStatus(SessionStatus.IN_PROGRESS); // 使用IN_PROGRESS替代ACTIVE
        session.setSessionStartTime(LocalDateTime.now()); // 修改这里，使用setSessionStartTime而不是setCreatedTime
        session.setLastMessageSentTime(LocalDateTime.now());
        
        // 保存会话
        Long sessionId = sessionService.createSession(session);
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        
        return ResponseEntity.ok(response);
    }
    
    // 获取咨询师的活跃会话列表
    @GetMapping("/chats")
    public ResponseEntity<?> getActiveSessions(@RequestParam Long counselorId) {
        List<ConsultationSession> sessions = sessionService.getActiveSessionsByCounselorId(counselorId);
        List<Map<String, Object>> result = sessions.stream().map(session -> {
            Map<String, Object> chatInfo = new HashMap<>();
            chatInfo.put("id", session.getSessionId());
            chatInfo.put("userId", session.getUserId());
            
            // 这里需要从用户服务获取用户名，暂时使用模拟数据
            chatInfo.put("userName", "用户" + session.getUserId());
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
            
            // 移除获取未读消息数量的代码
            // int unreadCount = messageService.getUnreadMessageCountForCounselor(session.getSessionId());
            // chatInfo.put("unreadCount", unreadCount);
            
            // 为保持前端兼容性，设置unreadCount为0
            chatInfo.put("unreadCount", 0);
            
            // 用户状态，这里简单返回在线状态
            chatInfo.put("status", "online");
            
            return chatInfo;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }

    // 获取特定会话的详细信息和历史消息
    @GetMapping("/chats/{sessionId}")
    public ResponseEntity<?> getSessionDetails(@PathVariable Long sessionId) {
        ConsultationSession session = sessionService.getSessionById(sessionId);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 获取会话消息
        List<SessionMessage> messages = messageService.getMessagesBySessionId(sessionId);
        
        // 将消息转换为前端需要的格式
        List<Map<String, Object>> formattedMessages = messages.stream().map(message -> {
            Map<String, Object> formattedMessage = new HashMap<>();
            formattedMessage.put("sender", message.getSenderRole().toString().toLowerCase());
            formattedMessage.put("text", message.getMessageContent());
            
            // 格式化时间为 HH:mm
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            formattedMessage.put("time", message.getMessageSentTime().format(formatter));
            
            return formattedMessage;
        }).collect(Collectors.toList());
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", session.getSessionId());
        response.put("userId", session.getUserId());
        
        // 这里需要从用户服务获取用户名，暂时使用模拟数据
        response.put("userName", "用户" + session.getUserId());
        response.put("type", "心理咨询");
        response.put("messages", formattedMessages);
        response.put("status", session.getSessionStatus().toString());
        
        // 标记消息为已读 - 移除此行，因为我们已经删除了这个功能
        // messageService.markMessagesAsReadForCounselor(sessionId);
        
        return ResponseEntity.ok(response);
    }

    // 发送消息
    @PostMapping("/chats/{sessionId}/messages")
    public ResponseEntity<?> sendMessage(
            @PathVariable Long sessionId,
            @RequestParam Long counselorId,
            @RequestParam String content) {
        
        // 检查会话是否存在
        ConsultationSession session = sessionService.getSessionById(sessionId);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 创建消息
        SessionMessage message = new SessionMessage();
        message.setSessionId(sessionId);
        message.setSenderRole(SenderRole.counselor);
        message.setSenderId(counselorId);
        message.setMessageType(MessageType.text);
        message.setMessageContent(content);
        message.setMessageSentTime(LocalDateTime.now());
        
        // 保存消息
        messageService.saveMessage(message);
        
        // 更新会话的最后消息时间
        sessionService.updateLastMessageTime(sessionId);
        
        // 格式化时间为 HH:mm
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = message.getMessageSentTime().format(formatter);
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("messageId", message.getMessageId());
        response.put("time", formattedTime);
        
        return ResponseEntity.ok(response);
    }

    // 更新会话状态（暂停/继续）
    @PutMapping("/chats/{sessionId}/status")
    public ResponseEntity<?> updateSessionStatus(
            @PathVariable Long sessionId,
            @RequestParam String status) {
        
        // 检查会话是否存在
        ConsultationSession session = sessionService.getSessionById(sessionId);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 更新状态
        SessionStatus newStatus;
        try {
            newStatus = SessionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("无效的状态值");
        }
        
        sessionService.updateSessionStatus(sessionId, newStatus);
        
        return ResponseEntity.ok().build();
    }

    // 结束会话
    @PutMapping("/chats/{sessionId}/end")
    public ResponseEntity<?> endSession(
            @PathVariable Long sessionId,
            @RequestParam(required = false) String reason) {
        
        // 检查会话是否存在
        ConsultationSession session = sessionService.getSessionById(sessionId);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 结束会话
        sessionService.endSession(sessionId);
        
        // 如果提供了结束原因，保存为系统消息
        if (reason != null && !reason.trim().isEmpty()) {
            SessionMessage message = new SessionMessage();
            message.setSessionId(sessionId);
            message.setSenderRole(SenderRole.system);
            message.setSenderId(0L); // 系统消息发送者ID为0
            message.setMessageType(MessageType.text);
            message.setMessageContent("会话已结束，原因：" + reason);
            message.setMessageSentTime(LocalDateTime.now());
            
            messageService.saveMessage(message);
        }
        
        return ResponseEntity.ok().build();
    }

    // 保存咨询笔记
    @PostMapping("/users/{userId}/notes")
    public ResponseEntity<?> saveUserNote(
            @PathVariable Long userId,
            @RequestParam Long counselorId,
            @RequestParam String noteContent) {
        
        // 这里需要实现保存笔记的逻辑，可能需要创建新的实体类和服务
        // 暂时返回成功
        return ResponseEntity.ok().build();
    }
}