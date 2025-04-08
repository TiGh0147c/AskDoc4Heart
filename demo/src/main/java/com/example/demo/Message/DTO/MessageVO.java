package com.example.demo.Message.DTO;
import lombok.Data;

@Data
public class MessageVO {
    private Integer messageId;
    private Integer sessionId;
    private String senderRole;
    private Integer senderId;
    private String messageType;
    private String messageContent;
    private String sentTime;
    private String senderName;
    private String senderAvatar;
}